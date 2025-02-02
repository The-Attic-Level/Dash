package com.the_attic_level.dash.ui.layout.type

import android.view.Gravity

typealias AlignX = UIAlign.Horizontal
typealias AlignY = UIAlign.Vertical

class UIAlign
{
    // ----------------------------------------
    // Horizontal
    
    enum class Horizontal
    {
        LEFT, CENTER, RIGHT;
        
        companion object
        {
            fun get(gravity: Int): Horizontal
            {
                val gx = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
                val gr = gravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK
                
                return if (gx == Gravity.CENTER_HORIZONTAL) {
                    CENTER
                } else if (gr == Gravity.END || gx == Gravity.RIGHT) {
                    RIGHT
                } else {
                    LEFT
                }
            }
        }
    }
    
    // ----------------------------------------
    // Vertical
    
    enum class Vertical
    {
        TOP, CENTER, BOTTOM;
        
        companion object {
            fun get(gravity: Int): Vertical {
                return when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                    Gravity.CENTER_VERTICAL -> CENTER
                    Gravity.BOTTOM -> BOTTOM
                    else -> TOP
                }
            }
        }
    }
    
    // ----------------------------------------
    // Static
    
    companion object {
        fun getX(gravity: Int) = Horizontal.get(gravity)
        fun getY(gravity: Int) = Vertical  .get(gravity)
    }
}