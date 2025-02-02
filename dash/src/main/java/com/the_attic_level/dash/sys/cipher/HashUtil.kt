package com.the_attic_level.dash.sys.cipher

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac

class HashUtil
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        val SHA_256_DIGEST: MessageDigest? by lazy {
            getHashFunction("SHA-256")
        }
        
        fun getHashFunction(algorithm: String): MessageDigest? {
            return try {
                MessageDigest.getInstance(algorithm)
            } catch (ignore: Exception) {
                null
            }
        }
        
        fun getMacFunction(algorithm: String): Mac? {
            return try {
                Mac.getInstance(algorithm)
            } catch (ignore: Exception) {
                null
            }
        }
        
        fun sha256(input: String): ByteArray? {
            return sha256(input, StandardCharsets.UTF_8)
        }
        
        fun sha256(input: String, charset: Charset): ByteArray? {
            return sha256(input.toByteArray(charset))
        }
        
        fun sha256(input: ByteArray): ByteArray? {
            return sha256(input, 0, input.size)
        }
        
        fun sha256(input: ByteArray, offset: Int, len: Int): ByteArray? {
            val digest = SHA_256_DIGEST ?: return null
            synchronized(digest) {
                digest.reset()
                digest.update(input, offset, len)
                return digest.digest()
            }
        }
    }
}