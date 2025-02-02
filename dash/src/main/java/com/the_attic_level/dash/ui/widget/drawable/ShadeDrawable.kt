package com.the_attic_level.dash.ui.widget.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.the_attic_level.dash.ui.layout.res.UIDrawable
import com.the_attic_level.dash.ui.layout.res.UIR

class ShadeDrawable(val orientation: Orientation, val resource: UIR? = null, val transparency: Int = 32): UIDrawable()
{
    // ----------------------------------------
    // Enum
    
    enum class Orientation {
        TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private val src = Rect()
    
    // ----------------------------------------
    // Members
    
    private var bitmap: Bitmap? = null
    
    // ----------------------------------------
    // Methods
    
    override fun create(): UIDrawable {
        return ShadeDrawable(this.orientation, this.resource, this.transparency)
    }
    
    override fun draw(canvas: Canvas)
    {
        setup()
        
        val bitmap = this.bitmap
        
        if (bitmap != null)
        {
            val bw = bitmap.width
            val bh = bitmap.height
            
            this.src.set(0, 0, bw, bh)
            
            canvas.drawBitmap(bitmap, this.src, this.bounds, getSharedPaint())
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun setup()
    {
        if (this.bitmap != null) {
            return
        }
        
        val w  = this.bounds.width()
        val h  = this.bounds.height()
        var bw = 1
        var bh = 1
        
        if (this.orientation == Orientation.TOP_BOTTOM || this.orientation == Orientation.BOTTOM_TOP) {
            bh = h
        } else {
            bw = w
        }
        
        // calculate pixel count
        val size = bw * bh
        
        // create pixel array
        val p = IntArray(size)
        
        // get color components only
        var color = 0
        
        if (this.resource != null) {
            color = this.resource.color and 0x00FFFFFF
        }
        
        // setup the orientation (from where to where we're interpolating)
        
        var src = 0.0F
        var dst = 0.0F
        
        if (this.orientation == Orientation.TOP_BOTTOM || this.orientation == Orientation.LEFT_RIGHT) {
            src = this.transparency.toFloat() // full alpha at top|left
        } else {
            dst = this.transparency.toFloat() // full alpha at bottom/right
        }
        
        // calculate the gradient
        
        val max = (size - 1).toFloat()
        var scale: Float
        
        for (i in 0 until size) {
            scale = i.toFloat() / max
            p[i] = (src + scale * (dst - src)).toInt() shl 24 or color
        }
        
        // create bitmap with out pixel array
        this.bitmap = Bitmap.createBitmap(p, bw, bh, Bitmap.Config.ARGB_8888)
    }
}