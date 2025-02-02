package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon

class CheckIcon(
    val a: Float = 0.15F, // thickness
    val b: Float = 0.45F, // width
    val c: Float = 0.75F  // height
): CachedIcon()
{
    // ----------------------------------------
    // Icon
    
    override fun onMeasure(params: Icon.Params)
    {
        val a = params.floor(this.a)
        val b = params.floor(this.b)
        val c = params.floor(this.c)
        
        params.apply(b+c, a+c)
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params)
    {
        val w = params.w
        val h = params.h
        val a = params.floor(this.a)
        val b = params.floor(this.b)
        
        val path = painter.path
        
        path.begin()
        path.line(0, h-b)
        path.line(a, h-b-a)
        path.line(b, h-a-a)
        path.line(w-a, 0)
        path.line(w, a)
        path.line(b, h)
        path.draw()
    }
}