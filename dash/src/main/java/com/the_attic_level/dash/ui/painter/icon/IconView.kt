package com.the_attic_level.dash.ui.painter.icon

import android.content.Context
import android.graphics.Canvas
import android.view.View

open class IconView(context: Context): View(context)
{
    // ----------------------------------------
    // Members (Final)
    
    @Suppress("LeakingThis")
    val painter = IconPainter(parent = this)
    
    // ----------------------------------------
    // View
    
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        onDrawIcon(canvas)
    }
    
    override fun setPressed(pressed: Boolean) {
        if (this.painter.isStateful) {
            invalidate()
        }
        super.setPressed(pressed)
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    open fun onDrawIcon(canvas: Canvas) {
        this.painter.draw(canvas, 0, 0, this.width, this.height)
    }
}