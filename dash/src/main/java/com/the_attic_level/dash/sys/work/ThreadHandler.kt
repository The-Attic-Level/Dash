package com.the_attic_level.dash.sys.work

import android.os.SystemClock
import com.the_attic_level.dash.app.Dash

abstract class ThreadHandler(
    val name    : String,
    val sleep   : Long = DEFAULT_THREAD_SLEEP,
    val timeout : Long = DEFAULT_THREAD_TIMEOUT)
{
    // ----------------------------------------
    // Static
    
    companion object {
        var DEFAULT_THREAD_SLEEP   : Long = 50L    // milliseconds
        var DEFAULT_THREAD_TIMEOUT : Long = 2_000L // milliseconds
    }
    
    // ----------------------------------------
    // Class
    
    private inner class Worker(name: String): Thread(name)
    {
        @Volatile
        var run = true
        
        override fun run() {
            while (this.run) {
                onWorkerUpdate(this)
            }
            onWorkerFinished()
        }
    }
    
    // ----------------------------------------
    // Members
    
    private var worker : Worker? = null
    private var timer  : Long    = 0L
    
    // ----------------------------------------
    // Methods
    
    fun start() {
        if (this.worker == null) {
            synchronized(this) {
                if (this.worker == null && this.isWorkerRequired) {
                    this.timer = 0
                    this.worker = Worker(this.name)
                    this.worker?.start()
                }
            }
        }
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    protected abstract val isWorkerRequired: Boolean
    protected abstract fun onUpdate()
    
    // ----------------------------------------
    // Worker Events
    
    private fun canStopWorker(): Boolean {
        return Dash.currentActivity == null || !this.isWorkerRequired
    }
    
    private fun onWorkerUpdate(worker: Worker)
    {
        onUpdate()
        
        if (this.timer > 0) {
            if (SystemClock.elapsedRealtime() >= this.timer) {
                synchronized(this) {
                    this.timer = 0
                    if (canStopWorker()) {
                        worker.run = false
                    }
                }
            }
        } else if (canStopWorker()) {
            synchronized(this) {
                // set timeout to stop worker
                this.timer = SystemClock.elapsedRealtime() + this.timeout
            }
        }
        
        // await sleep delay
        if (worker.run) {
            Dash.sleep(this.sleep)
        }
    }
    
    private fun onWorkerFinished() {
        synchronized(this) {
            this.worker = null
            start()
        }
    }
}