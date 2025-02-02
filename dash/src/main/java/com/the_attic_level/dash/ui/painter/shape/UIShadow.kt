package com.the_attic_level.dash.ui.painter.shape

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter

class UIShadow(val innerRadius: Int, val outerRadius: Int, color: Int): UIShape
{
    // ----------------------------------------
    // Members (Final)
    
    private val color: Int
    private val alpha: Int
    
    private val src = Rect()
    private val dst = Rect()
    
    // ----------------------------------------
    // Members
    
    private var bitmap: Bitmap? = null
    
    // ----------------------------------------
    // Init
    
    init
    {
        // check of the given color ist just the alpha value
        val alpha = Color.alpha(color)
        
        if (alpha == 0 && color > 0 && color <= 255) {
            this.color = 0
            this.alpha = color
        } else {
            this.color = color and 0x00FFFFFF
            this.alpha = alpha
        }
    }
    
    // ----------------------------------------
    // UI Shape
    
    override fun draw(painter: UIPainter, box: UIBox)
    {
        if (this.bitmap == null)
        {
            createBitmap()
            
            if (this.bitmap == null) {
                return
            }
        }
        
        val x = box.x
        val y = box.y
        val w = box.w
        val h = box.h
        
        val offset = 0
        
        val r = this.bitmap!!.width / 2
        val a = r + offset
        val d = r + r
        
        // corners
        draw(painter, /* src */ 0, 0, r, r, /* dst */ x,         y,         r, r) // top left
        draw(painter, /* src */ a, 0, r, r, /* dst */ x + w - r, y,         r, r) // top right
        draw(painter, /* src */ 0, a, r, r, /* dst */ x,         y + h - r, r, r) // bottom left
        draw(painter, /* src */ a, a, r, r, /* dst */ x + w - r, y + h - r, r, r) // bottom right
        
        // bars
        draw(painter, /* src */ r - 1, 0, 2, r, /* dst */ x + r,     y,         w - d, r) // top
        draw(painter, /* src */ r - 1, a, 2, r, /* dst */ x + r,     y + h - r, w - d, r) // bottom
        draw(painter, /* src */ 0, r - 1, r, 2, /* dst */ x,         y + r,     r, h - d) // left
        draw(painter, /* src */ a, r - 1, r, 2, /* dst */ x + w - r, y + r,     r, h - d) // right
    }
    
    // ----------------------------------------
    // Draw
    
    private fun draw(painter: UIPainter, u: Int, v: Int, s: Int, t: Int, x: Int, y: Int, w: Int, h: Int)
    {
        this.src.set(u, v, u + s, v + t)
        this.dst.set(x, y, x + w, y + h)
        
        painter.draw(this.bitmap!!, this.src, this.dst)
    }
    
    // ----------------------------------------
    // Render
    
    private fun createBitmap()
    {
        val radius = this.outerRadius
        
        if (radius <= 0) {
            return
        }
        
        // bitmap width and height
        val size = radius * 2
        
		// index of last pixels
        val s = size - 1
        
        var dx: Float
        var dy: Float
        var ln: Float
        
        var a: Int
        var c: Int
        
		// create pixel array
        val pixels = IntArray(size * size)
		
		// calculate radial gradient and mirror pixels for every corner
        
        for (y in 0 until radius)
		{
            for (x in 0 until radius)
			{
				dx = radius - x.toFloat()
				dy = radius - y.toFloat()
				
				// distance from corner to current pixel
				ln = UIMath.fastSqrt(dx * dx + dy * dy)
				
				// calculate alpha from distance
				a = getAlpha(ln)
				
				if (a > 0)
				{
					// create black color with alpha channel
					c = this.color or (a shl 24)
					
					pixels[     x  +      y  * size] = c // top left
					pixels[(s - x) +      y  * size] = c // top right
					pixels[     x  + (s - y) * size] = c // bottom left
					pixels[(s - x) + (s - y) * size] = c // bottom right
				}
			}
		}
		
		// create bitmap with pixels
		this.bitmap = Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun getAlpha(length: Float): Int
    {
        if (length <= this.innerRadius) {
            return this.alpha
        } else if (length >= this.outerRadius) {
            return 0
        }
        
        val scale = 1.0F - (length - this.innerRadius) / (this.outerRadius - this.innerRadius)
        return (scale * scale * scale * this.alpha.toFloat()).toInt()
    }
}