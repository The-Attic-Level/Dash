package com.the_attic_level.dash.sys.cipher

import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.the_attic_level.dash.app.DashApp
import com.the_attic_level.dash.sys.Logger
import java.security.Key
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

class CipherConfig(
    val algorithm  : String,
    val blockMode  : String,
    val padding    : String,
    val requiresIV : Boolean)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        // ------------------------------------
        // Purposes
        
        const val PURPOSE_ENCRYPT = KeyProperties.PURPOSE_ENCRYPT
        const val PURPOSE_DECRYPT = KeyProperties.PURPOSE_DECRYPT
        const val PURPOSE_BOTH = PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        
        // ------------------------------------
        // Android Keystore
        
        const val ANDROID_KEY_STORE_TYPE = "AndroidKeyStore"
        
        val ANDROID_KEY_STORE: KeyStore? by lazy {
            loadKeystore(ANDROID_KEY_STORE_TYPE)
        }
        
        /** Loads the keystore for the given type name. */
        fun loadKeystore(type: String): KeyStore? {
            return try {
                val keystore = KeyStore.getInstance(type)
                keystore.load(null)
                keystore
            } catch (e: Exception) {
                Logger.error(CipherConfig::class.java, "unable to load key store '$type'")
                null
            }
        }
        
        // ------------------------------------
        // Strongbox Availability
        
        /** Returns 'true' if the strongbox features for secret keys is available on the current device.  */
        val IS_STRONGBOX_AVAILABLE: Boolean by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                DashApp.shared.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
            } else false
        }
        
        // ------------------------------------
        // Default Configurations
        
        /**
          * KeyProperties.KEY_ALGORITHM_AES
          * KeyProperties.BLOCK_MODE_GCM
          * KeyProperties.ENCRYPTION_PADDING_NONE
          */
        val AES_GCM_NO_PADDING: CipherConfig by lazy {
            CipherConfig("AES", "GCM", "NoPadding", true)
        }
    }
    
    // ----------------------------------------
    // Members (Final)
    
    val mode = this.algorithm + '/' + this.blockMode + '/' + this.padding
    
    // ----------------------------------------
    // Cipher
    
    /**
     * Returns an initialized cipher instance for the given key.
     *
     * Note: The initialization vector (iv) is only required for decryption (mode = Cipher.DECRYPT_MODE) and only
     * if the key requires randomized encryption. When encrypting which such a key, the iv can be acquired from the
     * cipher instance.
     */
    fun initCipher(key: Key, iv: ByteArray?, mode: Int): Cipher?
    {
        var spec: AlgorithmParameterSpec? = null
        
        if (iv != null) {
            spec = if (this.blockMode == KeyProperties.BLOCK_MODE_GCM) {
                GCMParameterSpec(128, iv)
            } else {
                IvParameterSpec(iv)
            }
        }
        
        return try {
            val cipher = Cipher.getInstance(this.mode)
            cipher.init(mode, key, spec)
            cipher
        } catch (e: java.lang.Exception) {
            Logger.error(this, "unable to initialize cipher: " + e.message)
            null
        }
    }
    
    // ----------------------------------------
    // Key
    
    fun ensureKey(alias: String): Key?
    {
        val keystore = ANDROID_KEY_STORE ?: return generateKey(alias)
        
        try {
            if (keystore.containsAlias(alias)) {
                return keystore.getKey(alias, null)
            }
        } catch (ignore: java.lang.Exception) {
            Logger.error(this, "unable to obtain key")
        }
        
        return null
    }
    
    fun generateKey(alias: String): SecretKey? {
        return try {
            val generator = KeyGenerator.getInstance(this.algorithm, ANDROID_KEY_STORE_TYPE)
            generator.init(buildParams(alias))
            generator.generateKey()
        } catch (e: Exception) {
            Logger.error(this, "unable to generate key [$alias]: ${e.message} [${e.javaClass.simpleName}]")
            null
        }
    }
    
    fun buildParams(alias: String): KeyGenParameterSpec
    {
        val builder = KeyGenParameterSpec.Builder(alias, PURPOSE_BOTH)
            .setBlockModes(this.blockMode)
            .setEncryptionPaddings(this.padding)
            .setRandomizedEncryptionRequired(this.requiresIV)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && IS_STRONGBOX_AVAILABLE) {
            builder.setIsStrongBoxBacked(true)
        }
        
        return builder.build()
    }
}