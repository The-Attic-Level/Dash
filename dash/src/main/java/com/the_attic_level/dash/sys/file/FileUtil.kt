package com.the_attic_level.dash.sys.file

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FileUtil
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        fun read(file: File, out: OutputStream): Boolean {
            return read(file, out, null)
        }
        
        fun writeInt(out: OutputStream, i: Int) {
            out.write(i shr 24 and 0xFF)
            out.write(i shr 16 and 0xFF)
            out.write(i shr  8 and 0xFF)
            out.write(i        and 0xFF)
        }
        
        fun readInt(input: InputStream): Int {
            return (input.read() and 0xFF shl 24) or
                   (input.read() and 0xFF shl 16) or
                   (input.read() and 0xFF shl  8) or
                   (input.read() and 0xFF)
        }
        
        /** Reads the byte content of the given file into the given output stream. */
        fun read(file: File, out: OutputStream, buffer: ByteArray?): Boolean {
            try {
                FileInputStream(file).use { input ->
                    val bytes = buffer ?: ByteArray(256)
                    var read: Int
                    while (input.read(bytes).also { read = it } != -1) {
                        out.write(bytes, 0, read)
                    }
                    return true
                }
            } catch (ignore: Exception) {
                return false
            }
        }
        
        /** Closes the given stream while ignoring any exceptions. */
        fun close(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (ignore: IOException) {}
        }
        
        /**
         * Converts a byte array into a string where every non-default character will be replaced with '?'.
         * This can be used to determine whether a byte array represents a string or binary data.
         */
        fun parse(bytes: ByteArray?): String {
            if (bytes != null && bytes.isNotEmpty()) {
                val builder = StringBuilder(bytes.size)
                for (b in bytes) {
                    if (b in 33..126) {
                        builder.append(Char(b.toUShort()))
                    } else {
                        builder.append('?')
                    }
                }
                return builder.toString()
            }
            return ""
        }
    }
}