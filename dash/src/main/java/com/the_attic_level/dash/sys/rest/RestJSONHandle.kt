package com.the_attic_level.dash.sys.rest

import com.the_attic_level.dash.sys.rest.config.RestConfig
import okhttp3.Response
import org.json.JSONObject

abstract class RestJSONHandle(config: RestConfig, endpoint: RestEndpoint): RestHandle(config, endpoint)
{
    // ----------------------------------------
    // Rest Handle
    
    override fun onResponse(response: Response)
    {
        val content = try {
            response.body.string()
        } catch (e: Exception) {
            throw RestException("read error: ${e.message}")
        }
        
        val json = try {
            JSONObject(content)
        } catch (e: Exception) {
            throw RestException("json error: ${e.message}")
        }
        
        onResponse(json, response.code)
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    protected abstract fun onResponse(json: JSONObject, code: Int)
}