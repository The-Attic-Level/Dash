package com.the_attic_level.dash.ui.widget.view

import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.layout.type.UIRect

open class DashLayoutParams private constructor(width: Int=0, height: Int=0) : FrameLayout.LayoutParams(width, height)
{
    // ----------------------------------------
    // Members
    
    var weight   : Float = 0.0F
    var viewType : Int   = 0
    
    // ----------------------------------------
    // Factory
    
    companion object
    {
        // ------------------------------------
        // Private
        
        private val PARAMS = ArrayList<DashLayoutParams>(32)
        
        private fun acquire() : DashLayoutParams {
            if (PARAMS.isEmpty()) {
                return DashLayoutParams()
            }
            return PARAMS.removeLast()
        }
        
        private fun mismatch(cls: Class<*>, params: ViewGroup.LayoutParams?) {
            val src = cls.canonicalName
            val dst = if (params != null) params.javaClass.canonicalName else "<null>"
            Logger.error(DashLayoutParams::class.java, "layout parameter mismatch [required: $src] [found: $dst]")
        }
        
        // ------------------------------------
        // Public
        
        fun create(width: Int=0, height: Int=0, gravity: Int=-1, weight: Float=0.0F) : DashLayoutParams
        {
            val params = acquire()
            
            params.width   = width
            params.height  = height
            params.gravity = gravity
            params.weight  = weight
            
            return params
        }
        
        fun create(rect: UIRect) : DashLayoutParams
        {
            val params = acquire()
            
            params.leftMargin   = rect.left
            params.topMargin    = rect.top
            params.rightMargin  = rect.right
            params.bottomMargin = rect.bottom
            params.width        = rect.width
            params.height       = rect.height
            params.gravity      = rect.gravity
            params.weight       = rect.weight
            
            return params
        }
        
        fun checkLayoutParams(parent: ViewGroup, params: ViewGroup.LayoutParams?) : Boolean {
            // check if we can use the layout parameters directly instead of creating new ones
            // (this supports the frame layout, scroll view and horizontal scroll view)
            if (params is DashLayoutParams) {
                return parent is FrameLayout
            } else if (params is AbsListView.LayoutParams) {
                return parent is AbsListView
            }
            return false
        }
        
        fun generateLinearLayoutParams(params: ViewGroup.LayoutParams?) : LinearLayout.LayoutParams {
            if (params is DashLayoutParams) {
                return params.createLinearLayoutParams()
            } else if (params is LinearLayout.LayoutParams) {
                return params
            }
            mismatch(LinearLayout.LayoutParams::class.java, params)
            return LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        
        fun generateFrameLayoutParams(params: ViewGroup.LayoutParams?) : FrameLayout.LayoutParams {
            if (params is DashLayoutParams) {
                return params
            } else if (params is FrameLayout.LayoutParams) {
                return params
            }
            
            mismatch(FrameLayout.LayoutParams::class.java, params)
            return FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        
        fun generateAbsListViewParams(params: ViewGroup.LayoutParams?) : AbsListView.LayoutParams {
            if (params is DashLayoutParams) {
                return params.createAbsListViewParams()
            } else if (params is AbsListView.LayoutParams) {
                return params
            }
            mismatch(AbsListView.LayoutParams::class.java, params)
            return AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun createLinearLayoutParams() : LinearLayout.LayoutParams
    {
        val params = LinearLayout.LayoutParams(this.width, this.height, this.weight)
        
        params.leftMargin   = this.leftMargin
        params.topMargin    = this.topMargin
        params.rightMargin  = this.rightMargin
        params.bottomMargin = this.bottomMargin
        params.gravity      = this.gravity
        
        recycle()
        return params
    }
    
    private fun createAbsListViewParams() : AbsListView.LayoutParams {
        val params = AbsListView.LayoutParams(this.width, this.height, this.viewType)
        recycle()
        return params
    }
    
    private fun recycle()
    {
        this.width        = MATCH_PARENT
        this.height       = WRAP_CONTENT
        this.leftMargin   = 0
        this.topMargin    = 0
        this.rightMargin  = 0
        this.bottomMargin = 0
        this.gravity      = -1
        this.weight       = 0.0F
        this.viewType     = 0
        
        PARAMS.add(this)
    }
}