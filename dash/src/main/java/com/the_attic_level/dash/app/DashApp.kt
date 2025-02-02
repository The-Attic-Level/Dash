package com.the_attic_level.dash.app

import android.app.Activity
import android.app.Application
import com.the_attic_level.dash.sys.rest.NetworkUtil
import com.the_attic_level.dash.sys.sync.SyncHandler
import com.the_attic_level.dash.sys.work.WorkHandle
import com.the_attic_level.dash.sys.work.WorkHandler
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.activity.LifecycleEvent
import java.lang.ref.WeakReference

@Suppress("LeakingThis")
abstract class DashApp: Application()
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        /** Use for web requests. */
        const val WORK_TYPE_REST = "rest"
        
        /** Use for local file access. */
        const val WORK_TYPE_FILE = "file"
        
        /** Use for background processing. */
        const val WORK_TYPE_TASK = "task"
        
        /** Weak reference to avoid static field leaks. */
        private lateinit var instance: WeakReference<DashApp>
        
        val shared: DashApp
            get() = this.instance.get()!!
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private val workers = ArrayList<WorkHandler>(3)
    
    // ----------------------------------------
    // Members
    
    var activity: Activity? = null
        private set
    
    var count = 0
        private set
    
    // ----------------------------------------
    // Properties
    
    val style: UIStyle by lazy {
        UIStyle()
    }
    
    abstract val networkType: NetworkUtil.Type
    
    // ----------------------------------------
    // Init
    
    init {
        instance = WeakReference(this)
    }
    
    // ----------------------------------------
    // Application
    
    final override fun onCreate()
    {
        super.onCreate()
        
        // update UI metrics before any activities gets created
        updateLayout()
        
        // request optional network changes
        NetworkUtil.request(this.networkType)
        
        // pass application event
        onAppCreated()
    }
    
    // ----------------------------------------
    // Activity Lifecycle Events
    
    internal fun onInternalEvent(activity: Activity, event: LifecycleEvent) {
        when (event) {
            LifecycleEvent.ON_PRE_CREATE -> {
                updateLayout()
                onActivated(activity)
            }
            LifecycleEvent.ON_PRE_START -> {
                ++count
                if (count == 1) {
                    startWorkers()
                    onAppStarted()
                }
                onActivated(activity)
            }
            LifecycleEvent.ON_POST_STOP -> {
                onDeactivated(activity)
                onActivityEvent(activity, event.activityEvent!!)
                --this.count
                if (this.count == 0) {
                    onAppStopped()
                }
            }
            else -> {
                if (event.activityEvent != null) {
                    onActivityEvent(activity, event.activityEvent)
                }
            }
        }
    }
    
    fun onInternalResult(activity: Activity) {
        onActivated(activity)
    }
    
    // ----------------------------------------
    // Background Tasks
    
    fun schedule(type: String, handle: WorkHandle, listener: WorkHandle.Listener? = null) {
        getScheduler(type)?.schedule(handle, listener)
    }
    
    private fun getScheduler(type: String): WorkHandler? {
        if (type.isNotEmpty()) {
            synchronized(this.workers) {
                for (worker in this.workers) {
                    if (worker.name == type) {
                        return worker
                    }
                }
                val worker = WorkHandler(type)
                this.workers.add(worker)
                return worker
            }
        }
        return null
    }
    
    // ----------------------------------------
    // Abstract
    
    abstract fun onAppCreated()
    abstract fun onAppStarted()
    abstract fun onAppStopped()
    abstract fun onLayoutChanged()
    
    // ----------------------------------------
    // Activity Events
    
    protected open fun onActivityChanged(previous: Activity?, active: Activity?) {
        // override to handle event
    }
    
    protected open fun onActivityEvent(activity: Activity, event: ActivityEvent) {
        // override to handle event
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun onActivated(activity: Activity) {
        val before = this.activity
        this.activity = activity
        if (before !== activity) {
            onActivityChanged(before, activity)
        }
    }
    
    private fun onDeactivated(activity: Activity) {
        if (this.activity === activity) {
            this.activity = null
            onActivityChanged(activity, null)
        }
    }
    
    private fun updateLayout() {
        if (UI.update(this)) {
            onLayoutChanged()
        }
    }
    
    private fun startWorkers()
    {
        SyncHandler.start()
        
        synchronized(this.workers) {
            for (worker in this.workers) {
                worker.start()
            }
        }
    }
}