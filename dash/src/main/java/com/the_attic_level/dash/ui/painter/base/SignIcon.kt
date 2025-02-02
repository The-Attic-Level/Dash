package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon

class SignIcon(
    val style: Style,
    val scale: Float = 0.2F
): CachedIcon()
{
    // ----------------------------------------
    // Enum
    
    enum class Style { PLUS, MINUS }
    
    // ----------------------------------------
    // Icon
    
    override fun onMeasure(params: Icon.Params)
    {
        val a = params.scale(this.scale)
        val b = (params.size - a) / 2
        
        if (this.style == Style.PLUS) {
            params.apply(b + a + b)
        } else {
            params.apply(b + a + b, a)
        }
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params) {
        if (this.style == Style.PLUS) {
            val a = params.scale(this.scale)
            val b = (params.size - a) / 2
            painter.rect(params.x + b, params.y, a, params.h)
            painter.rect(params.x, params.y + b, params.w, a)
        } else {
            painter.rect(params)
        }
    }
}