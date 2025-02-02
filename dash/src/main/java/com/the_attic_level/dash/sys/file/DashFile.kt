package com.the_attic_level.dash.sys.file

import com.the_attic_level.dash.app.DashApp
import com.the_attic_level.dash.sys.cipher.CipherKey
import com.the_attic_level.dash.sys.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher

/**
 *  file structure:
 *  
 *  4 bytes file type: 'D', 'A', 'S', 'H'
 *  1 byte encryption mode (0 = no encryption, 1 = encrypted)
 *  1 byte iv-length
 *  x bytes encryption-iv
 *  4 bytes payload length
 *  x bytes payload
 */
abstract class DashFile(private val key: CipherKey? = null)
{
    // ----------------------------------------
    // Class
    
    /** A file located in the internal storage (app private). */
    class Internal(override val name: String, key: CipherKey? = null): DashFile(key)
    {
        val file: File by lazy {
            File(DashApp.shared.filesDir, this.name)
        }
        
        override fun getInput(): InputStream {
            return FileInputStream (this.file)
        }
        
        override fun getOutput(): OutputStream {
            return FileOutputStream(this.file)
        }
        
        override fun exists() = this.file.exists()
        override fun delete() = this.file.delete()
    }
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        private val FILE_TYPE = byteArrayOf(
            'D'.code.toByte(),
            'A'.code.toByte(),
            'S'.code.toByte(),
            'H'.code.toByte())
        
        private const val NOT_ENCRYPTED = 0
        private const val ENCRYPTED     = 1
        
        /** Creates a handle for a file in the local storage folder. */
        fun internal(name: String, key: CipherKey? = null): DashFile {
            return Internal(name, key)
        }
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private var buffer: ByteArray? = null
    
    // ----------------------------------------
    // Methods
    
    fun readBytes(): ByteArray? {
        synchronized(this) { return read() }
    }
    
    fun writeBytes(bytes: ByteArray?): Boolean {
        synchronized(this) { return write(bytes) }
    }
    
    fun readString(charset: Charset=StandardCharsets.UTF_8): String? {
        val bytes = readBytes()
        return if (bytes != null) {
            String(bytes, charset)
        } else null
    }
    
    fun writeString(value: String, charset: Charset = StandardCharsets.UTF_8): Boolean {
        return writeBytes(value.toByteArray(charset))
    }
    
    // ----------------------------------------
    // Abstract
    
    abstract val name: String
    
    abstract fun exists(): Boolean
    abstract fun delete(): Boolean
    
    // ----------------------------------------
    // Abstract (Protected)
    
    protected abstract fun getInput () : InputStream
    protected abstract fun getOutput() : OutputStream
    
    // ----------------------------------------
    // Read
    
    private fun read(): ByteArray? {
        var input: InputStream? = null
        return try {
            input = getInput()
            validateFileHeader(input)
            if (input.read() == ENCRYPTED) {
                readEncrypted(input)
            } else {
                readPlainText(input)
            }
        } catch (e: FileNotFoundException) {
            null
        } catch (e: Exception) {
            Logger.error(this, "[${this.name}] unable to read bytes: ${e.message}")
            null
        } finally {
            FileUtil.close(input)
        }
    }
    
    private fun readEncrypted(input: InputStream): ByteArray
    {
        val ivLength = input.read()
        var iv: ByteArray? = null
        
        if (ivLength > 0) {
            iv = ByteArray(ivLength)
            if (input.read(iv) != ivLength) {
                throw IOException("iv length mismatch")
            }
        } else if (this.key!!.config.requiresIV) {
            throw IOException("missing iv")
        }
        
        // initialize decryption cipher
        val cipher = this.key!!.initDecryption(iv)
            ?: throw IOException("unable to initialize decryption cipher")
        
        // read and decrypt payload
        val length: Int = FileUtil.readInt(input)
        
        if (length <= 0) {
            throw IOException("invalid payload length: $length")
        }
        
        val buffer = getBuffer(length)
        
        if (input.read(buffer, 0, length) != length) {
            throw IOException("decrypted payload length mismatch")
        }
        
        return cipher.doFinal(buffer, 0, length)
    }
    
    private fun readPlainText(input: InputStream): ByteArray
    {
        if (input.read() != 0) {
            throw IOException("plaintext files should not contain an iv")
        }
        
        val length: Int = FileUtil.readInt(input)
        
        if (length < 0) {
            throw IOException("invalid input length '$length'")
        } else if (length == 0) {
            return byteArrayOf()
        }
        
        // create byte array for payload
        val bytes = ByteArray(length)
        
        if (input.read(bytes) != length) {
            throw IOException("plaintext input length mismatch")
        }
        
        return bytes
    }
    
    // ----------------------------------------
    // Write
    
    private fun write(bytes: ByteArray?): Boolean {
        var output: OutputStream? = null
        return try {
            output = getOutput()
            output.write(FILE_TYPE)
            
            if (bytes == null || bytes.isEmpty()) {
                writeEmpty(output)
            } else if (this.key != null) {
                writeEncrypted(bytes, output, this.key)
            } else {
                writePlainText(bytes, output)
            }
            true
        } catch (e: Exception) {
            Logger.error(this, "[${this.name}] unable to write bytes: ${e.message}")
            false
        } finally {
            FileUtil.close(output)
        }
    }
    
    private fun writeEncrypted(bytes: ByteArray, output: OutputStream, key: CipherKey)
    {
        output.write(ENCRYPTED)
        
        // initialize encryption cipher
        val cipher: Cipher = key.initEncryption()
            ?: throw IOException("unable to initialize encryption cipher")
        
        // write initialization vector
        if (key.config.requiresIV)
        {
            val iv = cipher.iv
            
            if (iv == null || iv.isEmpty()) {
                throw IOException("invalid iv for decryption")
            }
            
            output.write(iv.size)
            output.write(iv)
        } else {
            output.write(0)
        }
        
        // write encrypted payload
        val buffer = getBuffer(cipher.getOutputSize(bytes.size))
        val length = cipher.doFinal(bytes, 0, bytes.size, buffer, 0)
        
        if (length > 0)
        {
            // write payload length
            FileUtil.writeInt(output, length)
            
            // write payload bytes
            output.write(buffer, 0, length)
        } else {
            throw IOException("encryption output length mismatch")
        }
    }
    
    private fun writePlainText(bytes: ByteArray, output: OutputStream)
    {
        output.write(NOT_ENCRYPTED)
        output.write(0)
        
        // write payload length
        FileUtil.writeInt(output, bytes.size)
        
        // write payload bytes
        output.write(bytes)
    }
    
    private fun writeEmpty(output: OutputStream)
    {
        output.write(NOT_ENCRYPTED)
        output.write(0)
        
        FileUtil.writeInt(output, 0)
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun getBuffer(size: Int): ByteArray {
        var buffer = this.buffer
        if (buffer == null || buffer.size < size) {
            buffer = ByteArray(size)
            this.buffer = buffer
        }
        return buffer
    }
    
    private fun validateFileHeader(input: InputStream) {
        for (b in FILE_TYPE) {
            if (b != input.read().toByte()) {
                throw IOException("file type mismatch")
            }
        }
    }
}