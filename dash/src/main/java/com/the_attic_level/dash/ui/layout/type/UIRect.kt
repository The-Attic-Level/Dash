package com.the_attic_level.dash.ui.layout.type

import android.view.View
import android.view.ViewGroup
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.widget.view.DashLayoutParams

class UIRect(
    var left       : Int   = 0,
    var top        : Int   = 0,
    var right      : Int   = 0,
    var bottom     : Int   = 0,
    var width      : Int   = MATCH_PARENT,
    var height     : Int   = MATCH_PARENT,
    var background : UIR?  = null,
    var gravity    : Int   = 0,
    var weight     : Float = 0.0F)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        private const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        
        val ZERO         = UIRect(width = 0, height = 0)
        val WRAP         = UIRect(width = WRAP_CONTENT, height = WRAP_CONTENT)
        val MATCH        = UIRect(width = MATCH_PARENT, height = MATCH_PARENT)
        val WRAP_MATCH   = UIRect(width = WRAP_CONTENT, height = MATCH_PARENT)
        val MATCH_WRAP   = UIRect(width = MATCH_PARENT, height = WRAP_CONTENT)
        val EQUAL_DIST_X = UIRect(width = 0, height = MATCH_PARENT, weight = 1.0F)
        val EQUAL_DIST_Y = UIRect(width = MATCH_PARENT, height = 0, weight = 1.0F)
        
        // equal distribution for horizontal linear layouts
        fun equalDistX(height: Int=MATCH_PARENT, weight: Float=1.0F) =
            UIRect(width=0, height=height, weight=weight)
        
        // equal distribution for vertical linear layouts
        fun equalDistY(width: Int=MATCH_PARENT, weight: Float=1.0F) =
            UIRect(width=width, height=0, weight=weight)
        
        fun fromSize(size: Int) =
            UIRect(width=size, height=size)
    }
    
    // ----------------------------------------
    // Init
    
    /** initialize a zero rectangle */
    constructor():
            this(left=0, top=0, width=0, height=0)
    
    /** initialize a rectangle with an uniform size */
    constructor(size: Int):
            this(left=0, top=0, width=size, height=size)
    
    /** initialize a rectangle with specific dimensions */
    constructor(width: Int, height: Int):
            this(left=0, top=0, width=width, height=height)
    
    /** initialize a zero rectangle with a background */
    constructor(background: UIR?):
            this(left=0, top=0, width=0, height=0, background=background)
    
    // ----------------------------------------
    // Methods
    
    fun setSize(size: Int) {
        this.width  = size
        this.height = size
    }
    
    fun setSize(width: Int, height: Int) {
        this.width  = width
        this.height = height
    }
    
    fun apply(view: View)
    {
        // apply layout parameters
        view.layoutParams = DashLayoutParams.create(this)
        
        // disable state list animator
        view.stateListAnimator = null
        
        // set background resource
        this.background?.let {
            UI.background(view, it)
        }
        
        // set default padding
        view.setPadding(0, 0, 0, 0)
    }
}