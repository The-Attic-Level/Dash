package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon

class CrossIcon(
    val a: Float = 0.35F, // length
    val b: Float = 0.15F  // thickness
): CachedIcon()
{
    // ----------------------------------------
    // Icon
    
    override fun onMeasure(params: Icon.Params) {
        val a = params.floor(this.a)
        val b = params.floor(this.b)
        params.apply(a+a+b+b)
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params)
    {
        val w = params.w
        val h = params.h
        val b = params.floor(this.b)
        
        val path = painter.path
        
        path.begin()
        path.move(0, b)
        path.line(b, 0)
        path.line(w, h-b)
        path.line(w-b, h)
        
        path.move(b, h)
        path.line(0, h-b)
        path.line(w-b, 0)
        path.line(w, b)
        path.draw()
    }
}