package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon

class DrawerIcon(
    val type: Type,
    val a: Float=0.2F,
    val b: Float=1.15F
): CachedIcon()
{
    // ----------------------------------------
    // Enum
    
    enum class Type { MENU, GRID, LIST }
    
    // ----------------------------------------
    // Cached Icon
    
    override fun onMeasure(params: Icon.Params)
    {
        val a = params.scale(this.a)
        val b = params.scale(this.b)
        val g = (params.size - 3 * a) / 2
        val h = 3 * a + 2 * g
        val w = 2 * g + (b - 2 * g) / 3 * 3
        
        params.apply(w, h)
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params)
    {
        when (this.type)
        {
            Type.MENU ->
            {
                val a = params.scale(this.a)
                val b = (params.h - 3 * a) / 2
                
                painter.rect(params.x, params.y, params.w, a)
                painter.rect(params.x, params.y + a + b, params.w, a)
                painter.rect(params.x, params.y + 2 * (a + b), params.w, a)
            }
            
            Type.GRID ->
            {
                val a = params.scale(this.a)
                val g = (params.size - 3 * a) / 2
                val c = (params.w - 2 * g) / 3
                val d = (params.h - 2 * g) / 3
                var y = 0
                
                while (y < 3) {
                    var x = 0
                    while (x < 3) {
                        painter.rect(params.x + x * (c + g), params.y + y * (d + g), c, d)
                        ++x
                    }
                    ++y
                }
            }
            
            Type.LIST ->
            {
                val a = params.scale(this.a)
                val b = (params.h - 3 * a) / 2
                val c = a + b
                var y = 0
                
                while (y < 3) {
                    painter.rect(params.x,     params.y + y * c,            a, a)
                    painter.rect(params.x + c, params.y + y * c, params.w - c, a)
                    ++y
                }
            }
        }
    }
}