package com.the_attic_level.dash.ui.painter.icon

import android.graphics.Bitmap
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.UIBox

interface Icon
{
    // ----------------------------------------
    // Params
    
    class Params: UIBox()
    {
        var size: Int = 0
        
        fun apply() {
            this.w = this.size
            this.h = this.size
        }
        
        fun apply(size: Int) {
            this.w = size
            this.h = size
        }
        
        fun apply(w: Int, h: Int) {
            this.w = w
            this.h = h
        }
        
        fun floor(scale: Float) = UIMath.floor(this.size, scale)
        fun scale(scale: Float) = UIMath.round(this.size, scale)
        fun even (scale: Float) = UIMath.even (this.size, scale)
    }
    
    // ----------------------------------------
    // Style
    
    class Style(val icon: Icon, val color: UIR, val gravity: Int, val scale: Float)
    
    // ----------------------------------------
    // Methods
    
    fun getBitmap(size: Int): Bitmap?
}