package com.the_attic_level.dash.ui.painter.shape

import android.graphics.Bitmap
import android.graphics.Rect
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter

class UIFrame(val radius: Int, val stroke: Int=0, val render: Boolean=true): UIShape
{
    // ----------------------------------------
    // Members (Final)
    
    private val src = Rect()
    private val dst = Rect()
    
    // ----------------------------------------
    // Members
    
    private var bitmap: Bitmap? = null
    
    // ----------------------------------------
    // UI Shape
    
    override fun draw(painter: UIPainter, box: UIBox)
    {
        // draw without corner radius
        if (this.radius == 0) {
            if (this.stroke > 0) {
                painter.frame(box, this.stroke)
            } else {
                painter.rect(box)
            }
            return
        }
        
        val x = box.x
        val y = box.y
        val w = box.w
        val h = box.h
        val r = this.radius
        val d = this.radius * 2
        val s = this.stroke
        
        // draw without using a bitmap
        if (!this.render) {
            if (this.stroke > 0) {
                painter.rounded(x, y, w, h, radius=r, stroke=s)
            } else {
                painter.rounded(x, y, w, h, radius=r)
            }
            return
        }
        
        if (this.bitmap == null) {
            createBitmap()
        }
        
        // corner: top left
        painter.applyColorFilter()
        drawCorner(painter, x, y, x+r, y+r, 0, 0)
        
        // corner: top right
        drawCorner(painter, x+w-r, y, x+w, y+r, 1, 0)
        
        // corner: bottom left
        drawCorner(painter, x, y+h-r, x+r, y+h, 0, 1)
        
        // corner: bottom right
        drawCorner(painter, x+w-r, y+h-r, x+w, y+h, 1, 1)
        painter.clearColorFilter()
        
        // lines
        
        val path = painter.path
        path.begin()
        
        if (this.stroke > 0) {
            path.rect(x+r, y, w-d, s)     // top
            path.rect(x, y+r, s, h-d)     // left
            path.rect(x+w-s, y+r, s, h-d) // right
            path.rect(x+r, y+h-s, w-d, s) // bottom
        } else {
            path.rect(x, y+r, r, h-d)     // left
            path.rect(x+r, y, w-d, h)     // center
            path.rect(x+w-r, y+r, r, h-d) // right
        }
        
        path.draw()
    }
    
    // ----------------------------------------
    // Draw Corner
    
    private fun drawCorner(painter: UIPainter, l: Int, t: Int, r: Int, b: Int, u: Int, v: Int)
    {
        val ra = this.radius
        val sl = u * ra
        val st = v * ra
        
        this.src.set(sl, st, sl + ra, st + ra)
        this.dst.set(l, t, r, b)
        
        painter.draw(this.bitmap!!, this.src, this.dst)
    }
    
    // ----------------------------------------
    // Create Bitmap
    
    private fun createBitmap()
    {
        // calculate sizes
        
        val maxRadius = this.radius
        var minRadius = 0
        
        if (this.stroke > 0) {
            minRadius = maxRadius - this.stroke
        }
        
        // crate pixel array
        
        val stride = maxRadius * 2
        val pixels = IntArray(stride * stride)
        
        // generate bitmap
        
        val e = stride - 1
        var dx: Float
        var dy: Float
        var ln: Float
        var alpha: Float
        var c: Int
        
        for (y in 0 until maxRadius)
        {
            for (x in 0 until maxRadius)
            {
                dx = (maxRadius - x).toFloat()
                dy = (maxRadius - y).toFloat()
                ln = UIMath.fastSqrt(dx * dx + dy * dy)
                
                alpha = getAlpha(ln, minRadius.toFloat(), maxRadius.toFloat())
                
                if (alpha > 0.0F)
                {
                    c = (alpha * 255.0F).toInt() shl 24 or 0x00FFFFFF
                    
                    pixels[    x +      y  * stride] = c // top left
                    pixels[e - x +      y  * stride] = c // top right
                    pixels[    x + (e - y) * stride] = c // bottom left
                    pixels[e - x + (e - y) * stride] = c // bottom right
                }
            }
        }
        
        this.bitmap = Bitmap.createBitmap(pixels, stride, stride, Bitmap.Config.ARGB_8888)
    }
    
    private fun getAlpha(length: Float, min: Float, max: Float): Float {
        if (length > max) {
            // 51.0 | 50.75 |50.25 |50.0 (max)
            return if (length < max + 1.0F) {
                1.0F - (length - max)
            } else 0.0F
        }
        return if (length < min + 1.0F) {
            // 26.0 | 25.75 | 25.25 | 25.0 (min)
            if (length > min) {
                length - min
            } else 0.0F
        } else 1.0F
    }
}