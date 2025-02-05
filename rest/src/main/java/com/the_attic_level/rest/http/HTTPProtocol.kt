package com.the_attic_level.rest.http

enum class HTTPProtocol(val prefix: String)
{
    // ----------------------------------------
    // Cases
    
    HTTP  ("http://"),
    HTTPS ("https://")
}