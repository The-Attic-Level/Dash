package com.the_attic_level.dash.sys.sync

import android.os.SystemClock

class SyncTimer(val interval: Long)
{
    // ----------------------------------------
    // Members (Final)
    
    private var nextUpdate: Long = 0L
    
    // ----------------------------------------
    // Methods
    
    fun start() {
        this.nextUpdate = SystemClock.elapsedRealtime() + this.interval
    }
    
    fun update(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now >= this.nextUpdate) {
            this.nextUpdate = now + this.interval
            return true
        }
        return false
    }
}