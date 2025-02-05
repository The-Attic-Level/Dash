package com.the_attic_level.rest

import com.the_attic_level.dash.app.DashApp
import com.the_attic_level.dash.sys.work.WorkHandle
import com.the_attic_level.rest.config.RestConfig
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

abstract class RestTask(protected val config: RestConfig): WorkHandle()
{
    // ----------------------------------------
    // Methods (Protected)
    
    protected fun request(endpoint: RestEndpoint, body: RequestBody?, vararg args: Any): Response {
        return request(this.config, endpoint, body, *args)
    }
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        fun schedule(task: RestTask, listener: Listener? = null) {
            DashApp.shared.schedule(DashApp.WORK_TYPE_REST, task, listener)
        }
        
        fun request(config: RestConfig, endpoint: RestEndpoint, body: RequestBody?, vararg args: Any): Response
        {
            if (!DashApp.shared.isNetworkAvailable) {
                throw RestException("no network available")
            }
            
            // get http-client
            val client = config.httpClient
            
            // build request
            val request = config.build(endpoint, body, *args)
            
            // send request and receive response
            val response: Response = try {
                client.newCall(request).execute()
            } catch (e: IOException) {
                throw RestException("request failed: ${e.message}")
            }
            
            if (!response.isSuccessful) {
                throw RestException("request failed with status code: ${response.code}")
            }
            
            return response
        }
    }
}