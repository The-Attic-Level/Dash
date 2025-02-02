package com.the_attic_level.dash.sys.rest.config

import com.the_attic_level.dash.sys.rest.RestEndpoint
import com.the_attic_level.dash.sys.rest.RestException
import com.the_attic_level.dash.sys.rest.http.HTTPMethod
import com.the_attic_level.dash.sys.rest.http.HTTPProtocol
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

open class RestConfig(
    val client: ClientConfig,
    val server: ServerConfig,
    val header: HeaderConfig
){
    // ----------------------------------------
    // Properties
    
    val httpClient: OkHttpClient = this.client.httpClient
    
    // ----------------------------------------
    // Init
    
    constructor(protocol: HTTPProtocol, hostname: String, path: String,
                certs: Array<ClientConfig.Cert> = ClientConfig.NO_CERTS) : this(
        ClientConfig(hostname, certs), ServerConfig(protocol.prefix + hostname + path), HeaderConfig())
    
    // ----------------------------------------
    // Methods
    
    open fun build(endpoint: RestEndpoint, body: RequestBody?, vararg args: Any?): Request {
        return build(endpoint.method, endpoint.path, body, *args)
    }
    
    open fun build(method: HTTPMethod, path: String, body: RequestBody?, vararg args: Any?): Request
    {
        // create request build with http method and request body
        val builder: Request.Builder = build(method, body)
        
        // set the resource url
        builder.url(url(path, *args))
        
        // apply request headers
        this.header.apply(builder)
        
        return builder.build()
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected fun url(path: String, vararg args: Any?): String {
        return StringBuilder(128).append(this.server.url).append(String.format(path, *args)).toString()
    }
    
    protected fun build(method: HTTPMethod, body: RequestBody?): Request.Builder {
        if (body != null) {
            if (method.discardsRequestBody) {
                throw RestException("method $method must not have a request body")
            }
        } else if (method.requiresRequestBody) {
            throw RestException("method $method must have a request body")
        }
        return Request.Builder().method(method.name, body)
    }
}