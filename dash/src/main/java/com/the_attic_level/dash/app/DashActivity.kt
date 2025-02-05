package com.the_attic_level.dash.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.activity.ActivityResult
import com.the_attic_level.dash.ui.activity.ActivityState
import com.the_attic_level.dash.ui.activity.InsetRequest
import com.the_attic_level.dash.ui.activity.LifecycleEvent
import com.the_attic_level.dash.ui.activity.PermissionResult
import com.the_attic_level.dash.ui.fragment.DashFragment
import com.the_attic_level.dash.ui.fragment.FragmentEvent
import com.the_attic_level.dash.ui.widget.DashParent
import com.the_attic_level.dash.ui.widget.view.DashFrameLayout
import com.the_attic_level.dash.ui.window.DashWindow
import com.the_attic_level.dash.ui.window.WindowClass
import com.the_attic_level.dash.ui.window.WindowStyle
import com.the_attic_level.dash.ui.window.dialog.DashDialog
import com.the_attic_level.dash.ui.window.dialog.DialogContext
import com.the_attic_level.dash.ui.window.dialog.DialogListener
import com.the_attic_level.dash.ui.window.dropdown.DashDropdown
import com.the_attic_level.dash.ui.window.overlay.DashOverlay

abstract class DashActivity: Activity(), DashParent
{
    // ----------------------------------------
    // Interface
    
    interface Request
    {
        enum class Type {
            EVENT, PERMISSION, RESULT, INSETS
        }
        
        val type: Type
    }
    
    // ----------------------------------------
    // Members
    
    var state = ActivityState.INSTANTIATED
        private set
    
    // ----------------------------------------
    // Members (Private)
    
    private var contentView : ViewGroup? = null
    private var systemBars  : Insets?    = null
    
    // ----------------------------------------
    // Requests / Dialogs
    
    private val windows     = ArrayList<DashWindow>(4)
    private val controllers = ArrayList<DashController>(4)
    private val requests    = ArrayList<Request>(4)
    
    val overlay by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        DashOverlay(this)
    }
    
    // ----------------------------------------
    // Properties
    
    open val prefersEdgeToEdge: Boolean
        get() = true
    
    val isEdgeToEdgeEnabled: Boolean
        get() = UI.isEdgeToEdgeEnforced() || this.prefersEdgeToEdge
    
    // ----------------------------------------
    // Parent
    
    override val parent: DashParent?
        get() = null
    
    final override val activity: DashActivity
        get() = this
    
    final override val layout: ViewGroup?
        get() = this.contentView
    
    override fun onEvent(child: DashFragment, event: FragmentEvent) {
        // notify registered controllers
        for (controller in this.controllers) {
            controller.onEvent(child, event)
        }
    }
    
    // ----------------------------------------
    // Back Press Handling
    
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (!interceptBackPress()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
    
    open fun interceptBackPress(): Boolean {
        for (controller in this.controllers) {
            if (controller.onInterceptBackPress(this)) {
                return true
            }
        }
        return false
    }
    
    open fun forceBackPress() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
    
    // ----------------------------------------
    // Create Layout
    
    protected open fun setupWindowFeatures()
    {
        this.window.requestFeature(Window.FEATURE_NO_TITLE)
        
        if (this.isEdgeToEdgeEnabled)
        {
            WindowCompat.setDecorFitsSystemWindows(this.window, false)
            
            @Suppress("DEPRECATION")
            this.window.statusBarColor = Color.TRANSPARENT
            
            @Suppress("DEPRECATION")
            this.window.navigationBarColor = Color.TRANSPARENT
            
            val handled = booleanArrayOf(false)
            
            ViewCompat.setOnApplyWindowInsetsListener(this.window.decorView)
            {
                _, insets ->
                
                if (!handled[0])
                {
                    handled[0] = true
                    
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    this.systemBars = systemBars
                    onApplyWindowInsets(systemBars)
                    
                    // notify receivers about system bar insets
                    val iterator = this.requests.iterator()
                    
                    while (iterator.hasNext()) {
                        val request = iterator.next()
                        if (request is InsetRequest) {
                            request.receiver.onInsets(systemBars)
                            iterator.remove()
                        }
                    }
                }
                
                insets
            }
        }
    }
    
    protected open fun onApplyWindowInsets(systemBars: Insets) {
        // implement to use system bar insets
    }
    
    protected open fun createContentView(): ViewGroup {
        return DashFrameLayout(this)
    }
    
    /** Override to setup views. */
    protected abstract fun onCreate()
    
    // ----------------------------------------
    // Activity Lifecycle
    
    final override fun onCreate(bundle: Bundle?)
    {
        onEvent(LifecycleEvent.ON_PRE_CREATE)
        setupWindowFeatures()
        super.onCreate(bundle)
        
        this.contentView = createContentView()
        
        onCreate()
        setContentView(this.layout)
        onEvent(LifecycleEvent.ON_POST_CREATE)
    }
    
    final override fun onStart() {
        onEvent(LifecycleEvent.ON_PRE_START)
        super.onStart()
        onEvent(LifecycleEvent.ON_POST_START)
    }
    
    final override fun onResume() {
        onEvent(LifecycleEvent.ON_PRE_RESUME)
        super.onResume()
        onEvent(LifecycleEvent.ON_POST_RESUME)
    }
    
    final override fun onPause() {
        onEvent(LifecycleEvent.ON_PRE_PAUSE)
        super.onPause()
        onEvent(LifecycleEvent.ON_POST_PAUSE)
    }
    
    final override fun onStop() {
        onEvent(LifecycleEvent.ON_PRE_STOP)
        super.onStop()
        onEvent(LifecycleEvent.ON_POST_STOP)
    }
    
    final override fun onDestroy() {
        onEvent(LifecycleEvent.ON_PRE_DESTROYED)
        super.onDestroy()
        onEvent(LifecycleEvent.ON_POST_DESTROYED)
    }
    
    // ----------------------------------------
    // Lifecycle Events
    
    private fun onEvent(event: LifecycleEvent)
    {
        if (event.activityState != null) {
            this.state = event.activityState
        }
        
        DashApp.shared.onInternalEvent(this, event)
        
        if (event.activityEvent == null) {
            return
        }
        
        onEvent(event.activityEvent)
        
        // notify registered controllers
        for (controller in this.controllers) {
            controller.onEvent(this, event.activityEvent)
        }
        
        // notify event receivers
        
        if (this.requests.isEmpty()) {
            return
        }
        
        val iterator = this.requests.iterator()
        
        while (iterator.hasNext()) {
            val request = iterator.next() as? ActivityEvent.Request
            if (request != null && request.event == event.activityEvent) {
                request.receiver.onEvent(this, request.event)
                iterator.remove()
            }
        }
    }
    
    /** Override to tap into the activity events. This is called after the lifecycle 'post' events. */
    protected open fun onEvent(event: ActivityEvent) {}
    
    // ----------------------------------------
    // Activity Result Handling
    
    fun requestActivityResult(intent: Intent, receiver: ActivityResult.Receiver): Int {
        try {
            val request = ActivityResult.Request(receiver)
            startActivityForResult(intent, request.requestCode)
            this.requests.add(request)
            return request.requestCode
        } catch (e: Exception) {
            Logger.error(this, "unable to start activity for result: ${e.message}")
        }
        return -1
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        DashApp.shared.onInternalResult(this)
        for (item in this.requests) {
            if (item.type == Request.Type.RESULT) {
                val request = item as ActivityResult.Request
                if (request.notify(this, requestCode, resultCode, data)) {
                    this.requests.remove(request)
                    return
                }
            }
        }
    }
    
    // ----------------------------------------
    // Permissions Handling
    
    fun requestPermissions(permissions: Array<String>, receiver: PermissionResult.Receiver): Int {
        val request = PermissionResult.Request(receiver)
        ActivityCompat.requestPermissions(this, permissions, request.requestCode)
        this.requests.add(request)
        return request.requestCode
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        DashApp.shared.onInternalResult(this)
        for (item in this.requests) {
            if (item.type == Request.Type.PERMISSION) {
                val request = item as PermissionResult.Request
                if (request.notify(this, requestCode, permissions, results)) {
                    this.requests.remove(request)
                    return
                }
            }
        }
    }
    
    // ----------------------------------------
    // Request Events / Insets
    
    fun requestEvent(event: ActivityEvent, receiver: ActivityEvent.Receiver) {
        this.requests.add(ActivityEvent.Request(receiver, event))
    }
    
    fun requestInsets(receiver: InsetRequest.Receiver) {
        val insets = this.systemBars
        if (insets != null) {
            receiver.onInsets(insets)
        } else if (this.isEdgeToEdgeEnabled) {
            this.requests.add(InsetRequest(receiver))
        }
    }
    
    // ----------------------------------------
    // Dialog / Dropdown
    
    fun showDialog(context: DialogContext, listener: DialogListener?): DashDialog?
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "dialogs must be started from main thread")
            return null
        }
        
        var dialog = getReusableWindow(context.cls, context.style) as? DashDialog
        
        if (dialog == null) {
            dialog = DashDialog.create(this, context.cls, context.style)
        }
        
        dialog?.show(context, listener)
        return dialog
    }
    
    fun findActiveDialog(context: DialogContext): DashDialog?
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "dialogs must be accessed from main thread")
            return null
        }
        
        for (window in this.windows) {
            if (window is DashDialog) {
                if (window.isShowing && window.context === context) {
                    return window
                }
            }
        }
        
        return null
    }
    
    fun showDropdown(builder: DashDropdown.Builder, listener: DashDropdown.Listener)
    {
        if (Dash.onMainThread())
        {
            // find reusable dropdown before creating a new instance
            var dropdown = getReusableWindow(builder.cls, builder.style) as? DashDropdown
            
            if (dropdown == null) {
                dropdown = DashDropdown.create(builder.cls, builder.style, this)
            }
            
            dropdown?.show(builder, listener)
        }
        else {
            Logger.warn(this, "dropdown must be accessed from main thread")
        }
    }
    
    fun getActiveWindow(cls: WindowClass, style: WindowStyle? = null): DashWindow? {
        return findWindow { it.isShowing && it.matches(cls, style) }
    }
    
    fun getReusableWindow(cls: WindowClass, style: WindowStyle? = null): DashWindow? {
        return findWindow { !it.isShowing && it.matches(cls, style) }
    }
    
    fun findWindow(selector: (DashWindow) -> Boolean): DashWindow? {
        for (window in this.windows) {
            if (selector.invoke(window)) {
                return window
            }
        }
        return null
    }
    
    fun storeReusableWindow(window: DashWindow) {
        if (!this.windows.contains(window)) {
            this.windows.add(window)
        }
    }
    
    // ----------------------------------------
    // Util
    
    protected fun register(controller: DashController) {
        if (!this.controllers.contains(controller)) {
            this.controllers.add(controller)
        } else {
            Logger.warn(this, "controller is already registered")
        }
    }
}