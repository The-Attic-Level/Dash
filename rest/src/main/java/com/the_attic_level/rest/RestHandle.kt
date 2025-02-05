package com.the_attic_level.rest

import com.the_attic_level.rest.config.RestConfig
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

abstract class RestHandle(config: RestConfig, protected val endpoint: RestEndpoint): RestTask(config)
{
    // ----------------------------------------
    // Work Handle
    
    final override fun onRun() {
        onResponse(request(this.endpoint, this.requestBody, *this.requestArguments))
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    protected abstract fun onResponse(response: Response)
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected open val requestString: String?
        get() = null
    
    protected open val requestArguments: Array<Any>
        get() = emptyArray()
    
    protected open val requestBody: RequestBody?
        get() = this.requestString?.toRequestBody(this.endpoint.type)
}