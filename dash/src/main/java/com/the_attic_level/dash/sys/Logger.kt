package com.the_attic_level.dash.sys

import android.util.Log
import kotlin.reflect.KClass

class Logger(val name: String)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        var TAG = "DASH"
        
        fun debug(src: Any?, msg: Any?) = log(Log.DEBUG, src, msg)
        fun info (src: Any?, msg: Any?) = log(Log.INFO,  src, msg)
        fun warn (src: Any?, msg: Any?) = log(Log.WARN,  src, msg)
        fun error(src: Any?, msg: Any?) = log(Log.ERROR, src, msg)
        
        private fun log(priority: Int, src: Any?, msg: Any?)
        {
            if (msg != null)
            {
                val source: String = when (src) {
                    is String    -> src
                    is Class<*>  -> src.simpleName
                    is KClass<*> -> src.simpleName ?: "KClass<?>"
                    is Any       -> src.javaClass.simpleName
                    else         -> "Unknown"
                }
                
                val message: String = when (msg) {
                    is Throwable -> "\n" + Log.getStackTraceString(msg)
                    is String    -> msg
                    else         -> msg.toString()
                }
                
                Log.println(priority, TAG, "[$source] $message")
            }
        }
    }
    
    // ----------------------------------------
    // Init
    
    constructor(cls: Class<*>) : this(cls.simpleName)
    
    // ----------------------------------------
    // Methods
    
    fun debug(msg: Any?) = debug(this.name, msg)
    fun info (msg: Any?) = info (this.name, msg)
    fun warn (msg: Any?) = warn (this.name, msg)
    fun error(msg: Any?) = error(this.name, msg)
}