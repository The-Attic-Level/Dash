package com.the_attic_level.dash.sys.rest

import com.the_attic_level.dash.sys.file.FileUtil
import com.the_attic_level.dash.sys.rest.config.RestConfig
import okhttp3.Response
import java.io.InputStream

abstract class RestStreamHandle(config: RestConfig, endpoint: RestEndpoint): RestHandle(config, endpoint)
{
    // ----------------------------------------
    // Rest Handle
    
    final override fun onResponse(response: Response)
    {
        val input = response.body.byteStream()
        
        try {
            onResponse(input, response.code)
        } finally {
            FileUtil.close(input)
        }
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    protected abstract fun onResponse(stream: InputStream, code: Int)
}