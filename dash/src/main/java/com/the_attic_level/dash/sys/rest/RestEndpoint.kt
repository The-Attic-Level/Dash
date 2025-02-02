package com.the_attic_level.dash.sys.rest

import com.the_attic_level.dash.sys.rest.http.HTTPMethod
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

interface RestEndpoint
{
    // ----------------------------------------
    // Static
    
    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
    
    // ----------------------------------------
    // Methods
    
    val method: HTTPMethod
    
    val type: MediaType
    
    val path: String
}