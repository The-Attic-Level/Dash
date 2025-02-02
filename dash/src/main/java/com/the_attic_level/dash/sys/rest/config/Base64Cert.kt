package com.the_attic_level.dash.sys.rest.config

import com.the_attic_level.dash.sys.Logger
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class Base64Cert(override val alias: String, val base64: String): ClientConfig.Cert
{
    // ----------------------------------------
    // Members (Final)
    
    override val x509: X509Certificate? by lazy {
        try {
            CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(
                this.base64.toByteArray(StandardCharsets.UTF_8))) as X509Certificate
        } catch (e: Exception) {
            Logger.error(this, "unable to generate X509 certificate [${this.alias}]: ${e.message}")
            null
        }
    }
}