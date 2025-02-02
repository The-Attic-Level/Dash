package com.the_attic_level.dash.ui.layout.type

import android.view.View
import com.the_attic_level.dash.ui.UI

class UIPadding(var left: Int=0, var top: Int=0, var right: Int=0, var bottom: Int=0)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        fun scaled(p: Int) =
            UIPadding(UI.scale(p))
        
        fun scaled(px: Int=0, py: Int=0) =
            UIPadding(UI.scale(px), UI.scale(py))
        
        fun scaled(left: Int=0, top: Int=0, right: Int=0, bottom: Int=0) =
            UIPadding(UI.scale(left), UI.scale(top), UI.scale(right), UI.scale(bottom))
    }
    
    // ----------------------------------------
    // Init
    
    constructor(p: Int): this(p, p, p, p)
    constructor(px: Int=0, py: Int=0): this(px, py, px, py)
    
    // ----------------------------------------
    // Methods
    
    fun apply(view: View) = view.setPadding(this.left, this.top, this.right, this.bottom)
}