package com.the_attic_level.dash.app

import com.the_attic_level.dash.sys.sync.AsyncTask
import com.the_attic_level.dash.sys.sync.SyncHandler
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.fragment.DashFragment
import java.lang.ref.WeakReference

abstract class DashController: AsyncTask.Callback
{
    // ----------------------------------------
    // Members
    
    /**
     * We only hold a weak reference here because the actual
     * task will be stored and managed by the SyncHandler.
     */
    private var task: WeakReference<AsyncTask>? = null
    
    // ----------------------------------------
    // Activity Events
    
    /** Return 'true' to prevent the back press event of the activity. */
    open fun onInterceptBackPress(activity: DashActivity): Boolean {
        return false
    }
    
    open fun onEvent(activity: DashActivity, event: ActivityEvent) {
        // override if needed
    }
    
    open fun onEvent(fragment: DashFragment, event: DashFragment.Event) {
        // override if needed
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected open fun invokeBackPress() {
        @Suppress("DEPRECATION")
        Dash.currentActivity?.onBackPressed()
    }
    
    // ----------------------------------------
    // Async Task
    
    protected fun startAsyncTask(interval: Long=0) {
        synchronized(this) {
            this.task?.get()?.stop()
            this.task = WeakReference(SyncHandler.task(interval, this))
        }
    }
    
    protected fun stopAsyncTask() {
        synchronized(this) {
            this.task?.get()?.stop()
            this.task = null
        }
    }
    
    override fun onAsync(task: AsyncTask) {
        // implement to handle background updates
    }
}