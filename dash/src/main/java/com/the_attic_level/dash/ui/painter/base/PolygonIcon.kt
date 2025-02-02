package com.the_attic_level.dash.ui.painter.base

import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.CachedIcon
import com.the_attic_level.dash.ui.painter.icon.Icon
import com.the_attic_level.dash.ui.layout.type.UIOrientation
import com.the_attic_level.dash.ui.painter.shape.UIPolygon

class PolygonIcon(
    points  : Int,
    detail  : Float,
    degrees : Double,
    val radius  : Float = 0.5F,
    val offsetX : Float = 0.0F,
    val offsetY : Float = 0.0F
): CachedIcon()
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        fun caret(orientation: UIOrientation, radius: Float = 0.55F, detail: Float = 0.18F, offset: Float = 0.1F): PolygonIcon
        {
            var offsetX = 0.0F
            var offsetY = 0.0F
            
            when (orientation) {
                UIOrientation.RIGHT -> offsetX = -offset
                UIOrientation.DOWN  -> offsetY = -offset
                UIOrientation.LEFT  -> offsetX =  offset
                UIOrientation.UP    -> offsetY =  offset
            }
            
            return PolygonIcon(3, detail, orientation.degrees, radius, offsetX, offsetY)
        }
    }
    
    // ----------------------------------------
    // Members (Final)
    
    val shape = UIPolygon(0.0F, points, degrees, detail, UIPolygon.Miter.MAINTAIN_TIPS)
    
    // ----------------------------------------
    // Icon
    
    override fun onMeasure(params: Icon.Params) {
        params.apply(params.size)
    }
    
    override fun onDraw(painter: UIPainter, params: Icon.Params)
    {
        val cx = params.cx + params.scale(this.offsetX)
        val cy = params.cy + params.scale(this.offsetY)
        
        this.shape.radius = params.floor(this.radius).toFloat()
        this.shape.draw(painter, cx, cy)
    }
}