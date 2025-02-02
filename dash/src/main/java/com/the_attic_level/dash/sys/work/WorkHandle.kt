package com.the_attic_level.dash.sys.work

import com.the_attic_level.dash.sys.Logger

abstract class WorkHandle
{
    // ----------------------------------------
    // Static
    
    companion object {
        var LOG_ERRORS = false
    }
    
    // ----------------------------------------
    // Interface
    
    fun interface Listener {
        fun onFinished(handle: WorkHandle)
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private var listener: Listener? = null
    
    // ----------------------------------------
    // Members
    
    var isFinished = false
        private set
    
    var error: String? = null
        private set
    
    // ----------------------------------------
    // Methods
    
    fun execute() = run()
    
    // ----------------------------------------
    // Internal
    
    /** The handle is being enqueued into a work handler.  */
    internal fun schedule(listener: Listener? = null)
    {
        this.listener = listener
        this.isFinished = false
        this.error = null
        
        onScheduled()
    }
    
    /** The handle is being updated by a work handler.  */
    internal fun run()
    {
        onStarted()
        
        try {
            onRun()
        } catch (e: Exception) {
            this.error = e.message
            onError(e)
        } finally {
            this.isFinished = true
            onFinished()
        }
    }
    
    // ----------------------------------------
    // Update
    
    /** Implement to prepare handle.  */
    protected open fun onScheduled() {
        // implement if needed
    }
    
    /** Implement to prepare the handle for the task.  */
    protected open fun onStarted() {
        // implement if needed
    }
    
    /** Implement to perform worker task. Returns 'true' if the task was successful.  */
    protected abstract fun onRun()
    
    /** Implement to cleanup resources.  */
    protected open fun onFinished() {
        if (this.listener != null) {
            this.listener?.onFinished(this)
            this.listener = null
        }
    }
    
    // ----------------------------------------
    // Methods
    
    /** called when an error occurred during the update  */
    protected fun onError(e: Exception) {
        if (LOG_ERRORS) {
            Logger.error(this, e.message)
        }
    }
}