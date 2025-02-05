package com.the_attic_level.dash.app

import android.app.Activity
import android.app.Application
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.sys.sync.SyncHandler
import com.the_attic_level.dash.sys.work.WorkHandle
import com.the_attic_level.dash.sys.work.WorkHandler
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.activity.LifecycleEvent
import java.lang.ref.WeakReference

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
    
    var currentActivity: Activity? = null
        private set
    
    var activityCount = 0
        private set
    
    var isNetworkAvailable = false
        private set
    
    var isNetworkMetered = false
        private set
    
    // ----------------------------------------
    // Properties
    
    open val style: UIStyle by lazy {
        UIStyle()
    }
    
    // ----------------------------------------
    // Init
    
    init {
        @Suppress("LeakingThis")
        instance = WeakReference(this)
    }
    
    // ----------------------------------------
    // Application
    
    final override fun onCreate()
    {
        super.onCreate()
        
        // update UI metrics before any activities get created
        updateLayout()
        
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
                ++this.activityCount
                if (this.activityCount == 1) {
                    startWorkers()
                    onAppStarted()
                }
                onActivated(activity)
            }
            LifecycleEvent.ON_POST_STOP -> {
                onDeactivated(activity)
                onActivityEvent(activity, event.activityEvent!!)
                --this.activityCount
                if (this.activityCount == 0) {
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
    // Abstract (Events)
    
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
    // Network Events
    
    protected open fun onNetworkAvailable(network: Network) {
        this.isNetworkAvailable = true
    }
    
    protected open fun onNetworkCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
        this.isNetworkMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }
    
    protected open fun onNetworkLost(network: Network) {
        this.isNetworkAvailable = false
    }
    
    // ----------------------------------------
    // Methods (Network)
    
    /** Requests network changes for wifi and cellular networks. */
    protected fun requestNetwork(wifi: Boolean, cellular: Boolean) {
        requestNetwork(NetworkRequest.Builder().also {
            it.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (wifi) {
                it.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            }
            if (cellular) {
                it.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }.build())
    }
    
    /** Requests network changes for the given requests, using the default callback. */
    protected fun requestNetwork(request: NetworkRequest)
    {
        requestNetwork(request, object: NetworkCallback()
        {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onNetworkAvailable(network)
            }
            
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, capabilities)
                onNetworkCapabilitiesChanged(network, capabilities)
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                onNetworkLost(network)
            }
        })
    }
    
    /** Uses the given request and callback to request network changes. */
    protected fun requestNetwork(request: NetworkRequest, callback: NetworkCallback)
    {
        // get connectivity manager
        val manager = ContextCompat.getSystemService(this,
            ConnectivityManager::class.java) ?: return
        
        try {
            manager.requestNetwork(request, callback)
        } catch (e: Exception) {
            Logger.error(this, "unable to request network: ${e.message}")
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun onActivated(activity: Activity) {
        val before = this.currentActivity
        this.currentActivity = activity
        if (before !== activity) {
            onActivityChanged(before, activity)
        }
    }
    
    private fun onDeactivated(activity: Activity) {
        if (this.currentActivity === activity) {
            this.currentActivity = null
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