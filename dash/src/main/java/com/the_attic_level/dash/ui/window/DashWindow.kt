package com.the_attic_level.dash.ui.window

import android.app.Activity
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.FrameLayout
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.window.overlay.DashOverlay

typealias WindowClass  = Class<out DashWindow>
typealias WindowStyle  = DashWindow.Style
typealias WindowParams = WindowManager.LayoutParams

abstract class DashWindow(val activity: Activity, open val style: Style)
{
    // ----------------------------------------
    // Interface
    
    interface Animator {
        fun createAnimation(reversed: Boolean): Animation
    }
    
    interface Background {
        fun createOutlines(): ViewOutlineProvider?
        fun createDrawable(): Drawable
    }
    
    interface Style
    {
        // ----------------------------------------
        // Properties
        
        /** Provides the enter and exit animations. */
        val animator: Animator
        
        /** Provides the panel outlines and background drawable. */
        val background: Background
        
        /** Provides the overlay color. */
        val dimColor: Int
            get() = DashOverlay.DEFAULT_COLOR
        
        /** Provides the overlay transparency. */
        val dimAmount: Float
            get() = DashOverlay.DEFAULT_ALPHA
        
        val panelWidth: Int
            get() = UI.MATCH_PARENT
        
        val panelHeight: Int
            get() = UI.WRAP_CONTENT
        
        val softInputMode: Int
            get() = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        
        // ----------------------------------------
        // Methods
        
        fun createParams(): FrameLayout.LayoutParams {
            return FrameLayout.LayoutParams(this.panelWidth, this.panelHeight, UI.CENTER)
        }
        
        fun applyDecoration(view: View)
        {
            // apply background drawable
            view.background = this.background.createDrawable()
            
            // apply optional outline provider
            val outline = this.background.createOutlines()
            
            if (outline != null) {
                view.clipToOutline   = true
                view.outlineProvider = outline
            }
        }
    }
    
    // ----------------------------------------
    // Enum
    
    enum class State {
        SHOW, VISIBLE, HIDE, HIDDEN
    }
    
    // ----------------------------------------
    // Members (Animations)
    
    /** Private animation listener. */
    private val animationListener = object: Animation.AnimationListener {
        override fun onAnimationStart (animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd   (animation: Animation) {
            this@DashWindow.onAnimationEnd()
        }
    }
    
    private val enterAnimation: Animation by lazy {
        this.style.animator.createAnimation(reversed=false).also {
            it.setAnimationListener(this.animationListener) }
    }
    
    private val exitAnimation: Animation by lazy {
        this.style.animator.createAnimation(reversed=true).also {
            it.setAnimationListener(this.animationListener) }
    }
    
    // ----------------------------------------
    // Members (Protected)
    
    protected var contentView: View? = null
        private set
    
    protected var state = State.HIDDEN
        private set
    
    // ----------------------------------------
    // Members (Private)
    
    private var windowView   : WindowView?   = null
    private var windowParams : WindowParams? = null
    
    /** Overlay ownership id. */
    private var overlay = 0L
    
    // ----------------------------------------
    // Properties
    
    val isShowing: Boolean
        get() = this.state != State.HIDDEN
    
    // ----------------------------------------
    // Methods
    
    open val isReusable: Boolean
        get() = true
    
    /** Checks if the window can be reused for the given class and style. */
    open fun matches(cls: WindowClass, style: Style?): Boolean {
        return this.javaClass == cls && (style == null || this.style === style)
    }
    
    // ----------------------------------------
    // Show / Hide
    
    fun show()
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to show window: not on main thread")
            return
        }
        
        if (this.state != State.HIDDEN) {
            return
        }
        
        if (this.contentView == null)
        {
            // create content view
            val contentView = onCreate()
            
            // exclude content view from outside clicks
            contentView.isClickable = true
            
            // setup content view layout params
            contentView.layoutParams = this.style.createParams()
            
            // setup background and optional outlines
            this.style.applyDecoration(contentView)
            
            // create window view
            val windowView = WindowView(this.activity)
            windowView.onBackPressed  = ::onBackPressed
            windowView.onOutsideClick = ::onOutsideClick
            windowView.addView(contentView)
            
            // create window layout params
            this.windowParams = onCreateWindowParams()
            this.windowParams?.softInputMode = this.style.softInputMode
            
            // apply window and content view
            this.windowView  = windowView
            this.contentView = contentView
        }
        
        // reset overlay id
        this.overlay = 0L
        
        // show optional background overlay
        if (this.style.dimAmount > 0.0F) {
            val activity = this.activity
            if (activity is DashActivity) {
                this.overlay = activity.overlay.show(
                    color = this.style.dimColor,
                    alpha = this.style.dimAmount)
            }
        }
        
        // store reusable window in activity
        if (this.isReusable && this.activity is DashActivity) {
            this.activity.storeReusableWindow(this)
        }
        
        // add view to window manager
        val manager = this.activity.getSystemService(WindowManager::class.java)
        manager.addView(this.windowView, this.windowParams)
        
        // change panel state
        this.state = State.SHOW
        onStateChanged()
        
        // start entry animation
        this.contentView?.startAnimation(this.enterAnimation)
    }
    
    fun hide()
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to hide panel: not on main thread")
            return
        }
        
        if (this.state != State.VISIBLE) {
            return
        }
        
        // change panel state
        this.state = State.HIDE
        onStateChanged()
        
        // start exit animation
        this.contentView?.startAnimation(this.exitAnimation)
        
        // hide optional background overlay
        if (this.overlay != 0L) {
            val activity = this.activity
            if (activity is DashActivity) {
                activity.overlay.hide(this.overlay)
            }
        }
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    protected abstract fun onCreate(): View
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected open fun onCreateWindowParams(): WindowParams {
        return WindowParams(
            UI.MATCH_PARENT, UI.MATCH_PARENT,
            WindowParams.TYPE_APPLICATION,
            WindowParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowParams.FLAG_HARDWARE_ACCELERATED or
                    WindowParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT
        )
    }
    
    protected open fun onStateChanged() {
        // override to handle state changes
    }
    
    protected open fun onOutsideClick() {
        hide()
    }
    
    protected open fun onBackPressed() {
        hide()
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun onAnimationEnd()
    {
        if (this.state == State.SHOW)
        {
            this.state = State.VISIBLE
            onStateChanged()
        }
        else if (this.state == State.HIDE)
        {
            // remove view from window manager
            val manager = this.activity.getSystemService(WindowManager::class.java)
            manager.removeView(this.windowView)
            
            this.state = State.HIDDEN
            onStateChanged()
        }
    }
}