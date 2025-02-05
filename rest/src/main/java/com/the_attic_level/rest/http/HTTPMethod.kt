package com.the_attic_level.rest.http

enum class HTTPMethod
{
    // ----------------------------------------
    // Cases
    
    /** requests a representation of the specified resource (should only retrieve data)  */
    GET,
    
    /** replaces all current representations of the target resource with the request payload  */
    PUT,
    
    /** asks for a response identical to that of a GET request, but without the response body  */
    HEAD,
    
    /** submits an entity to the specified resource (causing state changes or side effects on the server)  */
    POST,
    
    /** is used to apply partial modifications to a resource  */
    PATCH,
    
    /** deletes the specified resource  */
    DELETE;
    
    // ----------------------------------------
    // Properties
    
    /** whether this method is explicitly used without a request body  */
    val discardsRequestBody: Boolean
        get() = this == GET || this == HEAD
    
    /** whether this method is explicitly used with a request body  */
    val requiresRequestBody: Boolean
        get() = this == PUT || this == POST || this == PATCH
}