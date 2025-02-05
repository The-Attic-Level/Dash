package com.the_attic_level.rest.config

import com.the_attic_level.dash.sys.Logger
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

open class ClientConfig(val hostname: String, val certs: Array<Cert> = NO_CERTS)
{
    // ----------------------------------------
    // Interface
    
    interface Cert {
        val alias: String
        val x509: X509Certificate?
    }
    
    // ----------------------------------------
    // Static
    
    companion object {
        val NO_CERTS = arrayOf<Cert>()
    }
    
    // ----------------------------------------
    // Properties
    
    val httpClient: OkHttpClient by lazy {
        buildClient()
    }
    
    // ----------------------------------------
    // Build OK HTTP Client
    
    protected open fun buildClient(): OkHttpClient
    {
        val builder = OkHttpClient.Builder()
        
        // setup hostname verifier
        val hostnameVerifier = buildHostnameVerifier(hostname)
        
        if (hostnameVerifier != null) {
            builder.hostnameVerifier(hostnameVerifier)
        }
        
        // setup ssl socket factory with x509 trust manager
        val trustManager = buildTrustManager(this.certs)
        
        if (trustManager != null) {
            builder.sslSocketFactory(buildSocketFactory(trustManager), trustManager)
        }
        
        return builder.build()
    }
    
    // ----------------------------------------
    // Build Hostname Verifier
    
    protected open fun buildHostnameVerifier(hostname: String): HostnameVerifier? {
        return if (hostname.isNotEmpty()) {
            HostnameVerifier { s: String, _: SSLSession? -> hostname == s }
        } else null
    }
    
    // ----------------------------------------
    // Build Trust Manager
    
    protected open fun buildTrustManager(certs: Array<Cert>): X509TrustManager? {
        if (certs.isNotEmpty()) {
            try {
                // setup keystore
                val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
                keystore.load(null, null)
                for (cert in certs) {
                    val x509 = cert.x509
                    if (x509 != null) {
                        keystore.setCertificateEntry(cert.alias, x509)
                    }
                }
                
                // setup trust manager
                val factory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm()
                )
                
                factory.init(keystore)
                return factory.trustManagers[0] as X509TrustManager
            } catch (e: java.lang.Exception) {
                Logger.error(this, "unable to create trust manager: " + e.message)
            }
        }
        return null
    }
    
    // ----------------------------------------
    // Build Socket Factory
    
    protected open fun buildSocketFactory(trustManager: X509TrustManager?): SSLSocketFactory {
        if (trustManager != null) {
            try {
                val context = SSLContext.getInstance("TLS")
                context.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
                return context.socketFactory
            } catch (e: Exception) {
                Logger.error(this, "unable to create socket factory: ${e.message}")
            }
        }
        return SSLSocketFactory.getDefault() as SSLSocketFactory
    }
}