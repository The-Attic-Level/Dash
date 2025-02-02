package com.the_attic_level.dash.sys.cipher

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class CipherKey private constructor(val config: CipherConfig, val alias: String?, key: ByteArray?)
{
    // ----------------------------------------
    // Members
    
    /*
	 * The key that is used to initialize a cipher. Usually an instance of AndroidKeyStoreSecretKey, which is
	 * derived from android.security.keystore.AndroidKeyStoreKey: "This key does not export its key material ..."
	 */
    private var internalKey: Key? = null
    
    /* Whether there already was an attempt to retrieve the key from the keystore. */
    @Volatile private var initialized = false
    
    // ----------------------------------------
    // Properties
    
    val key: Key?; get() {
        if (!this.initialized) {
            synchronized(this) {
                if (!this.initialized) {
                    this.initialized = true
                    this.internalKey = this.config.ensureKey(this.alias!!)
                }
            }
        }
        return this.internalKey
    }
    
    // ----------------------------------------
    // Init
    
    init {
        if (key != null) {
            this.internalKey = SecretKeySpec(key, this.config.algorithm)
            this.initialized = true
        }
    }
    
    // ----------------------------------------
    // Factory
    
    companion object
    {
        /** Creates a key with the default config: CipherConfig.AES_GCM_NO_PADDING */
        fun createDefault(alias: String) = CipherKey(CipherConfig.AES_GCM_NO_PADDING, alias, null)
        
        /** Creates a key using the given config and alias. */
        fun create(config: CipherConfig, alias: String) = CipherKey(config, alias, null)
        
        /** Creates a key using the given config and secret. */
        fun create(config: CipherConfig, key: ByteArray) = CipherKey(config, null, key)
    }
    
    // ----------------------------------------
    // Cipher
    
    fun initEncryption(): Cipher? {
        return this.key?.let { this.config.initCipher(it, null, Cipher.ENCRYPT_MODE) }
    }
    
    fun initDecryption(iv: ByteArray?): Cipher? {
        return this.key?.let { this.config.initCipher(it, iv, Cipher.DECRYPT_MODE) }
    }
}