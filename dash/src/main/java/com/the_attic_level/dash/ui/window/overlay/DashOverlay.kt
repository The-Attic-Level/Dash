package com.the_attic_level.dash.ui.window.overlay

import android.app.Activity
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.res.UIColor
import com.the_attic_level.dash.ui.window.WindowParams

class DashOverlay(private val activity: Activity)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        const val DEFAULT_COLOR = Color.BLACK
        const val DEFAULT_ALPHA = 0.3F
        
        const val DEFAULT_SHOW_DURATION = 200L
        const val DEFAULT_HIDE_DURATION = 200L
        
        val DEFAULT_SHOW_INTERPOLATOR = DecelerateInterpolator()
        val DEFAULT_HIDE_INTERPOLATOR = AccelerateInterpolator()
    }
    
    // ----------------------------------------
    // Enum
    
    private enum class State {
        HIDDEN, SHOW, VISIBLE, HIDE
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private val params = WindowParams(
        WindowParams.TYPE_APPLICATION,
        WindowParams.FLAG_LAYOUT_IN_SCREEN or
                WindowParams.FLAG_LAYOUT_NO_LIMITS or
                WindowParams.FLAG_HARDWARE_ACCELERATED or
                WindowParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
        PixelFormat.TRANSLUCENT)
    
    private val background = OverlayBackground {
        this@DashOverlay.onAnimationFinished(it)
    }
    
    private val decor = FrameLayout(this.activity).also {
        it.background = this.background
    }
    
    // ----------------------------------------
    // Members
    
    private var state = State.HIDDEN
    private var owner = 0L
    
    // ----------------------------------------
    // Methods
    
    fun show(
        color        : Int          = DEFAULT_COLOR,
        alpha        : Float        = DEFAULT_ALPHA,
        duration     : Long         = DEFAULT_SHOW_DURATION,
        interpolator : Interpolator = DEFAULT_SHOW_INTERPOLATOR
    ): Long {
        return show(UIColor.applyAlpha(color, alpha), duration, interpolator)
    }
    
    fun show(
        color        : Int,
        duration     : Long         = DEFAULT_SHOW_DURATION,
        interpolator : Interpolator = DEFAULT_SHOW_INTERPOLATOR
    ): Long
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to show overlay: not on main thread")
            return 0
        }
        
        if (this.state == State.HIDDEN)
        {
            this.params.x       = 0
            this.params.y       = 0
            this.params.width   = this.activity.window.decorView.width
            this.params.height  = this.activity.window.decorView.height
            this.params.gravity = UI.LEFT_TOP
            
            val manager = this.activity.getSystemService(WindowManager::class.java)
            manager.addView(this.decor, this.params)
        }
        
        this.state = State.SHOW
        this.background.start(color, duration, interpolator)
        
        return ++ this.owner
    }
    
    fun hide(
        owner        : Long,
        duration     : Long         = DEFAULT_HIDE_DURATION,
        interpolator : Interpolator = DEFAULT_HIDE_INTERPOLATOR)
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to hide overlay: not on main thread")
            return
        }
        
        if (owner != this.owner) {
            return // invalid owner id
        }
        
        if (this.state != State.SHOW && this.state != State.VISIBLE) {
            return // invalid state
        }
        
        this.owner = 0L
        this.state = State.HIDE
        this.background.start(Color.TRANSPARENT, duration, interpolator)
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun onAnimationFinished(visible: Boolean) {
        if (visible) {
            this.state = State.VISIBLE
        } else {
            this.state = State.HIDDEN
            val manager = this.activity.getSystemService(WindowManager::class.java)
            manager.removeView(this.decor)
        }
    }
}