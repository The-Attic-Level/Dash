package com.the_attic_level.dash.ui.layout

import androidx.annotation.FloatRange
import com.the_attic_level.dash.ui.UI

interface UIMethod
{
    // ----------------------------------------
    // Default
    
    class Default: UIMethod
    {
        var size = 0.0F
        
        override fun floor(scale: Float) = UIMath.floor(this.size, scale)
        override fun scale(scale: Float) = UIMath.round(this.size, scale)
        override fun even (scale: Float) = UIMath.even (this.size, scale)
    }
    
    // ----------------------------------------
    // Methods
    
    /** The scaled result will be rounded down to the lower value. */
    fun floor(@FloatRange(from = 0.0, to = 1.0) scale: Float): Int
    
    /** The scaled result will be rounded to the nearest value. */
    fun scale(@FloatRange(from = 0.0, to = 1.0) scale: Float): Int
    
    /** The scaled result will be rounded up, if necessary, to achieve an even value. */
    fun even(@FloatRange(from = 0.0, to = 1.0) scale: Float): Int
    
    // ----------------------------------------
    // Methods (Default)
    
    /**
     * The scaled result will be rounded down to the lower value.
     * The size will be relative to the reference size.
     */
    fun floor(size: Int) = floor(size.toFloat() / UI.REFERENCE_SIZE)
    
    /**
     * The scaled result will be rounded to the nearest value.
     * The size will be relative to the reference size.
     */
    fun scale(size: Int) = scale(size.toFloat() / UI.REFERENCE_SIZE)
    
    /**
     * The scaled result will be rounded up, if necessary, to achieve an even value.
     * The size will be relative to the reference size.
     */
    fun even(size: Int) = even(size.toFloat() / UI.REFERENCE_SIZE)
}