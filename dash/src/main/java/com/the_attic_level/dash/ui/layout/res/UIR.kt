package com.the_attic_level.dash.ui.layout.res

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.the_attic_level.dash.ui.painter.shape.UIFrame
import com.the_attic_level.dash.ui.widget.drawable.ColorDrawable
import com.the_attic_level.dash.ui.widget.drawable.ShapeDrawable

interface UIR
{
    // ----------------------------------------
    // Enum
    
    enum class Type { COLOR, COLOR_LIST, DRAWABLE }
    
    // ----------------------------------------
    // Properties
    
    val type: Type
    
    val color: Int
        get() = Color.MAGENTA
    
    val list: ColorStateList?
        get() = null
    
    val drawable: Drawable?
        get() = null
    
    // ----------------------------------------
    // Methods
    
    fun color(state: IntArray): Int {
        val list = this.list
        if (list is UIColorList) {
            return list.get(state)
        }
        return this.color
    }
    
    fun color(enabled: Boolean, pressed: Boolean, selected: Boolean=false): Int {
        val list = this.list
        if (list is UIColorList) {
            return list.get(enabled, pressed, selected)
        }
        return this.color
    }
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        // standard colors
        
        val BLACK   = UIColor(Color.BLACK)
        val WHITE   = UIColor(Color.WHITE)
        val RED     = UIColor(Color.RED)
        val GREEN   = UIColor(Color.GREEN)
        val BLUE    = UIColor(Color.BLUE)
        val CYAN    = UIColor(Color.CYAN)
        val MAGENTA = UIColor(Color.MAGENTA)
        val YELLOW  = UIColor(Color.YELLOW)
        
        // color (rgb)
        
        fun rgb(c: Int) =
            UIColor(Color.rgb(c, c, c))
        
        fun rgb(r: Int, g: Int, b: Int) =
            UIColor(Color.rgb(r, g, b))
        
        fun argb(a: Int, c: Int) =
            UIColor(Color.argb(a, c, c, c))
        
        fun argb(a: Int, r: Int, g: Int, b: Int) =
            UIColor(Color.argb(a, r, g, b))
        
        // color (hvs)
        
        fun hsv(h: Int, s: Float, v: Float, alpha: Int=0xFF) : UIColor {
            return UIColor(Color.HSVToColor(alpha, floatArrayOf(h.toFloat(), s, v)))
        }
        
        // drawables
        
        fun drawable(color: UIR) = ColorDrawable(color)
        fun drawable(color: Int) = ColorDrawable(color)
        
        fun drawable(color: UIR, radius: Int=0, stroke: Int=0) = ShapeDrawable(color, UIFrame(radius, stroke))
        fun drawable(color: Int, radius: Int=0, stroke: Int=0) = ShapeDrawable(color, UIFrame(radius, stroke))
        
        // color static list
        
        fun list(enabled: UIR, pressed: UIR) =
            UIColorList(enabled, pressed)
        
        fun list(disabled: UIR, enabled: UIR, pressed: UIR) =
            UIColorList(disabled, enabled, pressed)
        
        fun list(disabled: UIR, enabled: UIR, pressed: UIR, selected: UIR) =
            UIColorList(disabled, enabled, pressed, selected)
    }
}