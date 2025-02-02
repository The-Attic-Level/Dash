package com.the_attic_level.dash.ui.widget

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.ternary
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.type.UIRect

open class DashLayer(final override val parent: DashParent): DashParent {
    // ----------------------------------------
    // Enum
    
    enum class Type  { FRAME, VERTICAL, HORIZONTAL, LIST, SCROLL_X, SCROLL_Y, GRID }
    enum class State { SHOW, VISIBLE, HIDE, HIDDEN }
    
    // ----------------------------------------
    // Constants
    
    companion object
    {
        var DEFAULT_ANIMATION_SPEED = 150L
        var DEFAULT_INTERPOLATOR: Interpolator = AccelerateDecelerateInterpolator()
        
        fun create(parent: DashParent, type: Type, rect: UIRect): DashLayer {
            val layer = DashLayer(parent)
            layer.setup(type, rect)
            layer.attach()
            return layer
        }
    }
    
    // ----------------------------------------
    // Members (View)
    
    private var view: ViewGroup? = null
    
    private val animationListener = object: AnimatorListener {
        override fun onAnimationStart (animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            this@DashLayer.onAnimationEnd()
        }
    }
    
    // ----------------------------------------
    // Parent
    
    final override val activity = this.parent.activity
    
    final override val layout: ViewGroup?
        get() = this.view
    
    // ----------------------------------------
    // Members (State)
    
    var state = State.HIDDEN
        private set
    
    // ----------------------------------------
    // Members
    
    /* Whether the last show/hide was animated or static (attach/detach) */
    var isAnimated = false; private set
    
    // ----------------------------------------
    // Properties
    
    val isVisible get() = this.state == State.VISIBLE
    val isHidden  get() = this.state == State.HIDDEN
    val isBusy    get() = this.state == State.SHOW || this.state == State.HIDE
    val isActive  get() = this.state == State.SHOW || this.state == State.VISIBLE
    val isCreated get() = this.view != null
    
    // ----------------------------------------
    // Methods
    
    open fun interceptBackPress() = false
    
    // ----------------------------------------
    // Show
    
    fun attach() {
        show(animate = false)
    }
    
    fun show(animate: Boolean = true)
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to show layer: not on main thread")
            return
        }
        
        if (this.state != State.HIDDEN) {
            return
        }
        
        if (this.view == null) {
            create()
        }
        
        val view = this.view ?: return
        
        // make sure the view is attached to its parent
        if (view.parent == null) {
            this.parent.addView(view)
            if (animate) {
                view.alpha = 0.0F
            }
        }
        
        // make sure the view is visible
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            if (animate) {
                view.alpha = 0.0F
            }
        }
        
        view.parent.bringChildToFront(view)
        
        if (animate && view.alpha == 0.0F) {
            update(State.SHOW, animated = true)
            createAnimation(view, reversed = false)
                .setListener(this.animationListener)
                .start()
        } else {
            update(State.SHOW,    animated = false)
            update(State.VISIBLE, animated = false)
        }
    }
    
    // ----------------------------------------
    // Hide
    
    fun detach() {
        hide(animate = false)
    }
    
    fun hide(animate: Boolean = true)
    {
        if (!Dash.onMainThread()) {
            Logger.warn(this, "unable to show layer: not on main thread")
            return
        }
        
        if (this.state != State.VISIBLE) {
            return
        }
        
        val view = this.view ?: return
        
        if (view.parent == null) {
            return
        }
        
        if (animate && view.alpha == 1.0F) {
            update(State.HIDE, animated = true)
            createAnimation(view, reversed = true)
                .setListener(this.animationListener)
                .start()
        } else {
            update(State.HIDE,   animated = false)
            update(State.HIDDEN, animated = false)
        }
    }
    
    // ----------------------------------------
    // Methods (Events)
    
    fun create() {
        if (this.view == null) {
            onCreate()
        }
    }
    
    fun update(param: Any? = null) {
        if (this.view != null) {
            onUpdate(param)
        }
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected fun setup(type: Type, rect: UIRect) {
        when (type) {
            Type.FRAME      -> setup(UI.frame           (this.activity, rect))
            Type.VERTICAL   -> setup(UI.vertical        (this.activity, rect))
            Type.HORIZONTAL -> setup(UI.horizontal      (this.activity, rect))
            Type.LIST       -> setup(UI.list            (this.activity, rect))
            Type.SCROLL_X   -> setup(UI.horizontalScroll(this.activity, rect))
            Type.SCROLL_Y   -> setup(UI.verticalScroll  (this.activity, rect))
            Type.GRID       -> setup(UI.grid            (this.activity, rect))
        }
    }
    
    protected fun setup(view: ViewGroup) {
        if (this.view != null) {
            Logger.warn(this, "layout was already set in layer")
        }
        this.state = State.HIDDEN
        this.view  = view
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected open fun onCreate() {
        // override to setup layout
    }
    
    protected open fun onUpdate(param: Any?) {
        // override to update content
    }
    
    protected open fun onStateChanged() {
        // override to handle state changes
    }
    
    protected open fun createAnimation(view: View, reversed: Boolean): ViewPropertyAnimator {
        return view.animate()
            .alpha(ternary(reversed, 0.0F, 1.0F))
            .setDuration(DEFAULT_ANIMATION_SPEED)
            .setInterpolator(DEFAULT_INTERPOLATOR)
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun update(state: State, animated: Boolean)
    {
        // check if states have changed
        if (this.state != state)
        {
            // apply new state
            this.state = state
            
            // apply animation flag
            this.isAnimated = animated
            
            if (state == State.VISIBLE) {
                this.view?.alpha = 1.0F
            } else if (state == State.HIDDEN) {
                this.parent.removeView(this.view)
            }
            
            onStateChanged()
        }
    }
    
    private fun onAnimationEnd() {
        if (this.state == State.SHOW) {
            update(State.VISIBLE, true)
        } else if (this.state == State.HIDE) {
            update(State.HIDDEN, true)
        }
    }
}