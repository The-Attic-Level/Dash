package com.the_attic_level.dash.sys.sync

import android.os.Handler
import android.os.Looper
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.sys.work.ThreadHandler

class SyncHandler: ThreadHandler("sync")
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        private val MAIN_HANDLER = Handler(Looper.getMainLooper())
        
        private val shared: SyncHandler by lazy {
            SyncHandler()
        }
        
        fun start() = this.shared.start()
        
        /** Runs the given action on the main thread. */
        fun ui(delay: Long=0, runnable: Runnable) {
            if (delay <= 0 && Dash.onMainThread()) {
                runnable.run()
            } else {
                MAIN_HANDLER.postDelayed(runnable, delay)
            }
        }
        
        /**
         * Runs the given action on the sync-thread, which will be shared with other tasks.
         * The action is only executed once.
         */
        fun async(delay: Long=0, runnable: Runnable) {
            this.shared.enqueue(DelayedTask(runnable, delay))
        }
        
        /**
         * Runs the given task on the sync-thread, which will be shared with other tasks.
         * A task will be executed until 'stop' is called, or until the app terminates.
         */
        fun task(interval: Long=0, callback: (AsyncTask) -> Unit) {
            val task = AsyncTask(object: AsyncTask.Callback {
                override fun onAsync(task: AsyncTask) {
                    callback(task)
                }
            }, interval)
            this.shared.enqueue(task)
        }
        
        /**
         * Runs the given task on the sync-thread, which will be shared with other tasks.
         * A task will be executed until 'stop' is called, or until the app terminates.
         */
        fun task(interval: Long=0, callback: AsyncTask.Callback): AsyncTask {
            val task = AsyncTask(callback, interval)
            this.shared.enqueue(task)
            return task
        }
        
        /** Runs the given action on its own thread. */
        fun thread(runnable: Runnable) {
            Thread(runnable).start()
        }
    }
    
    // ----------------------------------------
    // Interface
    
    interface Task {
        /** Contract: Returns 'true' to keep the task running. */
        fun update(): Boolean
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private val active = ArrayList<Task>(8)
    private val append = ArrayList<Task>(4)
    private val remove = ArrayList<Task>(2)
    
    // ----------------------------------------
    // Members
    
    @Volatile
    private var scheduler: Runnable? = null
    
    // ----------------------------------------
    // Methods
    
    fun setScheduler(scheduler: Runnable?) {
        this.scheduler = scheduler
        if (scheduler != null) {
            start()
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun enqueue(task: Task) {
        synchronized(this.append) {
            this.append.add(task)
        }
        start()
    }
    
    // ----------------------------------------
    // Thread Handler
    
    override val isWorkerRequired: Boolean
        get() = this.append.isNotEmpty() || this.active.isNotEmpty() || this.scheduler != null
    
    override fun onUpdate()
    {
        // append new tasks
        if (this.append.isNotEmpty()) {
            synchronized(this.append) {
                this.active.addAll(this.append)
                this.append.clear()
            }
        }
        
        // update active tasks
        if (this.active.isNotEmpty())
        {
            for (task in this.active) {
                if (task.update()) {
                    this.remove.add(task)
                }
            }
            
            if (this.remove.isNotEmpty()) {
                for (task in this.remove) {
                    this.active.remove(task)
                }
                this.remove.clear()
            }
        }
        
        // update scheduler
        this.scheduler?.run()
    }
}