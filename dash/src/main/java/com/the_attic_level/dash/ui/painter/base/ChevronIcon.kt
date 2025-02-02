package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.UIPathPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon
import com.the_attic_level.dash.ui.layout.type.UIOrientation

class ChevronIcon(
    val orientation: UIOrientation,
    val count: Int = 1,   // number of items
    val a: Float = 0.5F,  // height
    val b: Float = 0.15F, // thickness
    val c: Float = 0.28F  // offset
): CachedIcon()
{
    // ----------------------------------------
    // Members (Final)
    
    val swap   = (this.orientation == UIOrientation.UP    || this.orientation == UIOrientation.DOWN)
    val mirror = (this.orientation == UIOrientation.RIGHT || this.orientation == UIOrientation.DOWN)
    
    // ----------------------------------------
    // Icon
    
    override fun onMeasure(params: Icon.Params)
    {
        val a = params.floor(this.a)
        val b = params.floor(this.b)
        val c = params.floor(this.c)
        
        val size = a + b + (this.count - 1) * (b + b + c)
        
        if (this.orientation == UIOrientation.LEFT || this.orientation == UIOrientation.RIGHT) {
            params.apply(size, a+a)
        } else {
            params.apply(a+a, size)
        }
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params)
    {
        val a = params.floor(this.a)
        val b = params.floor(this.b)
        val c = params.floor(this.c)
        val s = a + b
        
        val path = painter.path
        path.begin()
        
        var x: Int
        val y = params.y
        
        for (i in 0 until this.count)
        {
            x = if (this.orientation == UIOrientation.LEFT || this.orientation == UIOrientation.UP) {
                params.x + i * (b + b + c)
            } else {
                params.x - i * (b + b + c)
            }
            
            path.move()
            push(path, s, x, y + a)
            push(path, s, x + a, y)
            push(path, s, x + a + b, y + b)
            push(path, s, x + b + b, y + a)
            push(path, s, x + a + b, y + a + a - b)
            push(path, s, x + a, y + a + a)
        }
        
        path.draw()
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun push(path: UIPathPainter, size: Int, x: Int, y: Int) {
        if (this.swap) {
            if (this.mirror) {
                path.line(y, size - x)
            } else {
                path.line(y, x)
            }
        } else if (this.mirror) {
            path.line(size - x, y)
        } else {
            path.line(x, y)
        }
    }
}