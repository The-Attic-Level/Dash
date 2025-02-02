package com.the_attic_level.dash.sys.sync

import kotlin.concurrent.Volatile

class AsyncTask(private val callback: Callback, interval: Long=0): SyncHandler.Task
{
    // ----------------------------------------
    // Interface
    
    interface Callback {
        fun onAsync(task: AsyncTask)
    }
    
    // ----------------------------------------
    // Members (Final)
    
    /** Create optional timer if task requires an interval. */
    private val timer = if (interval > 0) SyncTimer(interval) else null
    
    // ----------------------------------------
    // Members
    
    @Volatile private var runnable: Runnable? = null
    @Volatile private var posting = false
    @Volatile private var stopped = false
    
    // ----------------------------------------
    // Methods
    
    fun post(runnable: Runnable) {
        if (!this.stopped) {
            this.runnable = runnable
        }
    }
    
    fun stop(runnable: Runnable) {
        if (!this.stopped) {
            this.runnable = runnable
            this.stopped  = true
        }
    }
    
    fun stop() {
        if (!this.stopped) {
            this.runnable = null
            this.stopped  = true
        }
    }
    
    // ----------------------------------------
    // Sync Handler Task
    
    override fun update(): Boolean
    {
        if (!this.posting && !this.stopped && this.timer?.update() != false)
        {
            this.callback.onAsync(this)
            
            if (this.runnable != null)
            {
                // set foreground flag
                this.posting = true
                
                // run on main thread
                SyncHandler.ui()
                {
                    // get runnable
                    val action = this.runnable
                    
                    // execute runnable
                    action?.run()
                    
                    // release runnable
                    this.runnable = null
                    
                    // release foreground flag
                    this.posting = false
                }
            }
        }
        
        return this.stopped
    }
}