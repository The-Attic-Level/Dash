package com.the_attic_level.dash.sys.work

class WorkHandler(name: String): ThreadHandler(name)
{
    // ----------------------------------------
    // Members (Final)
    
    private val pending = ArrayList<WorkHandle>(8)
    
    // ----------------------------------------
    // Schedule
    
    /** Schedules the given handle. */
    fun schedule(handle: WorkHandle, listener: WorkHandle.Listener?) {
        synchronized(this) {
            if (!this.pending.contains(handle)) {
                handle.schedule(listener)
                this.pending.add(handle)
                start()
            }
        }
    }
    
    // ----------------------------------------
    // Thread Handler
    
    override val isWorkerRequired: Boolean
        get() = this.pending.isNotEmpty()
    
    override fun onUpdate()
    {
        if (this.pending.isNotEmpty())
        {
            val handle: WorkHandle?
            
            synchronized(this) {
                handle = this.pending.removeFirstOrNull()
            }
            
            handle?.run()
        }
    }
}