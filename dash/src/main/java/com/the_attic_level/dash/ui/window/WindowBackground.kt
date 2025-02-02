package com.the_attic_level.dash.ui.window

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.ViewOutlineProvider
import com.the_attic_level.dash.app.ternary
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.shape.UIFrame
import com.the_attic_level.dash.ui.painter.shape.UIShadow
import com.the_attic_level.dash.ui.painter.shape.UIShape

class WindowBackground(
    val background  : UIShape,
    val foreground  : UIShape,
    val shadowSize  : Int,
    val panelRadius : Int,
    val panelColor  : Int,
    val useOutlines : Boolean
): Drawable(), DashWindow.Background
{
    // ----------------------------------------
    // Members (Final)
    
    private val box = UIBox()
    
    // ----------------------------------------
    // Init
    
    constructor(
        shadowSize  : Int,
        shadowColor : Int,
        panelRadius : Int,
        panelColor  : Int,
        useOutlines : Boolean
    ): this(
        background  = UIShadow(panelRadius, panelRadius + shadowSize, shadowColor),
        foreground  = UIFrame (panelRadius, render=false),
        shadowSize  = shadowSize,
        panelRadius = panelRadius,
        panelColor  = panelColor,
        useOutlines = useOutlines
    )
    
    // ----------------------------------------
    // Dash Panel Background
    
    override fun createOutlines(): ViewOutlineProvider? {
        return UI.createOutline(ternary(this.useOutlines, this.panelRadius, 0))
    }
    
    override fun createDrawable(): Drawable {
        return WindowBackground(
            this.background,
            this.foreground,
            this.shadowSize,
            this.panelRadius,
            this.panelColor,
            this.useOutlines
        )
    }
    
    // ----------------------------------------
    // Drawable
    
    override fun draw(canvas: Canvas)
    {
        UIPainter.draw(canvas, this)
        {
            painter ->
            
            // draw background
            this.box.set(0, 0, painter.width, painter.height)
            this.background.draw(painter, this.box)
            
            // draw foreground
            this.box.inset(this.shadowSize)
            painter.color(this.panelColor)
            this.foreground.draw(painter, this.box)
        }
    }
    
    override fun setAlpha(alpha: Int) {
        // not used
    }
    
    override fun setColorFilter(filter: ColorFilter?) {
        // not used
    }
    
    override fun getPadding(padding: Rect): Boolean {
        val value = this.shadowSize
        if (value > 0) {
            padding.set(value, value, value, value)
            return true
        }
        return false
    }
    
    @Deprecated("deprecated by android", ReplaceWith("nothing"))
    override fun getOpacity() = PixelFormat.TRANSLUCENT
}