package com.the_attic_level.dash.ui.window.sheet

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.ViewOutlineProvider
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.window.DashWindow

class BottomSheetBackground(val draggable: Boolean): Drawable(), DashWindow.Background
{
    // ----------------------------------------
    // Dash Panel Background
    
    override fun createOutlines(): ViewOutlineProvider? {
        return UI.createOutline(0)
    }
    
    override fun createDrawable(): Drawable {
        return BottomSheetBackground(this.draggable)
    }
    
    // ----------------------------------------
    // Drawable
    
    override fun draw(canvas: Canvas)
    {
        val width  = this.bounds.width()
        val height = this.bounds.height()
        val radius = UI.scale(96)
        
        UIPainter.draw(canvas, this)
        {
            painter ->
            
            // draw background
            
            painter.color = Color.WHITE
            painter.path.begin()
            painter.path.arc(radius, radius, radius, 180.0F, 90.0F)
            painter.path.arc(width - radius, radius, radius, 270.0F, 90.0F)
            painter.path.line(width, height)
            painter.path.line(0, height)
            painter.path.draw()
            
            // draw handle
            
            if (this.draggable)
            {
                val handleW = UIMath.even(width, 0.3F)
                val handleH = UI.even(12)
                val handleX = (width - handleW) / 2
                val handleY = UI.scale(36)
                
                painter.color = Color.LTGRAY
                painter.rounded(handleX, handleY, handleW, handleH, handleH / 2)
            }
        }
    }
    
    override fun setAlpha(alpha: Int) {
        // not used
    }
    
    override fun setColorFilter(filter: ColorFilter?) {
        // not used
    }
    
    override fun getPadding(padding: Rect): Boolean {
        return false
    }
    
    @Deprecated("deprecated by android", ReplaceWith("nothing"))
    override fun getOpacity() = PixelFormat.TRANSLUCENT
}