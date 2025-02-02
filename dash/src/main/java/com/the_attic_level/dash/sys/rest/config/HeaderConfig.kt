package com.the_attic_level.dash.sys.rest.config

import okhttp3.Request

open class HeaderConfig
{
    // ----------------------------------------
    // Class
    
    class Entry(var key: String, var value: String, var enabled: Boolean = true)
    
    // ----------------------------------------
    // Members (Final)
    
    protected val entries = ArrayList<Entry>(8)
    
    // ----------------------------------------
    // Methods
    
    open operator fun set(key: String, value: String): HeaderConfig {
        if (key.isNotEmpty()) {
            for (entry in this.entries) {
                if (entry.key == key) {
                    entry.value = value
                    return this
                }
            }
            this.entries.add(Entry(key, value))
        }
        return this
    }
    
    open fun apply(builder: Request.Builder) {
        for (entry in this.entries) {
            if (entry.enabled && entry.value.isNotEmpty()) {
                builder.addHeader(entry.key, entry.value)
            }
        }
    }
}