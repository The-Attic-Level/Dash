package com.the_attic_level.dash.sys.sync

import android.os.SystemClock

class DelayedTask(private val runnable: Runnable, private val delay: Long=0): SyncHandler.Task
{
    // ----------------------------------------
    // Members (Final)
    
    private val runAt
        get() = if (this.delay > 0) SystemClock.elapsedRealtime() + this.delay else 0
    
    // ----------------------------------------
    // Sync Handler Task
    
    override fun update(): Boolean
    {
        // execute immediately or await delay
        if (this.runAt == 0L || SystemClock.elapsedRealtime() >= this.runAt)
        {
            this.runnable.run()
            
            // terminate task
            return true
        }
        
        // await delay
        return false
    }
}