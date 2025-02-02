package com.the_attic_level.dash.ui.widget

import android.view.View
import android.view.ViewGroup
import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.fragment.DashFragment
import com.the_attic_level.dash.ui.layout.res.UIR

interface DashParent
{
    // ----------------------------------------
    // Base
    
    val activity: DashActivity
    
    // ----------------------------------------
    // Properties
    
    val parent: DashParent?
    val layout: ViewGroup?
    
    // ----------------------------------------
    // Methods (Default)
    
    fun addView(view: View?) {
        val layout = this.layout
        if (view != null && layout != null && view.parent !== layout) {
            layout.addView(view)
        }
    }
    
    fun removeView(view: View?) {
        val layout = this.layout
        if (view != null && layout != null && view.parent === layout) {
            layout.removeView(view)
        }
    }
    
    // ----------------------------------------
    // Fragment Events
    
    /** Implement to receive events from child fragments. */
    fun onEvent(child: DashFragment, event: DashFragment.Event) {
        this.parent?.onEvent(child, event)
    }
    
    // ----------------------------------------
    // Methods (Utils)
    
    fun gravity(gravity: Int) =
        UI.gravity(this.layout, gravity)
    
    fun weight(weight: Float) =
        UI.weight(this.layout, weight)
    
    fun background(res: UIR) =
        UI.background(this.layout, res)
    
    fun <T: DashParent> findParent(cls: Class<T>): T? {
        var parent = this.parent
        while (parent != null) {
            if (cls.isInstance(parent)) {
                return cls.cast(parent)
            }
            parent = parent.parent
        }
        return null
    }
}