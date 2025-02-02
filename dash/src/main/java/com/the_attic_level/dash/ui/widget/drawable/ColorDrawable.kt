package com.the_attic_level.dash.ui.widget.drawable

import android.graphics.Canvas
import com.the_attic_level.dash.ui.layout.res.UIColor
import com.the_attic_level.dash.ui.layout.res.UIDrawable
import com.the_attic_level.dash.ui.layout.res.UIR

class ColorDrawable(resource: UIR = UIR.MAGENTA): UIDrawable()
{
    // ----------------------------------------
    // Members (Private)
    
    private var currentColor = 0
    
    // ----------------------------------------
    // Properties
    
    var resource: UIR = resource
        set(value) {
            field = value
            this.currentColor = 0
        }
    
    // ----------------------------------------
    // Init
    
    constructor(color: Int): this(UIColor(color))
    
    // ----------------------------------------
    // UI Resource
    
    override val color: Int
        get() {
            if (this.currentColor == 0) {
                onStateChange(this.state)
            }
            return this.currentColor
        }
    
    // ----------------------------------------
    // UI Drawable
    
    override fun create() = ColorDrawable(this.resource)
    
    // ----------------------------------------
    // Drawable
    
    override fun isStateful(): Boolean {
        return this.resource.type == UIR.Type.COLOR_LIST
    }
    
    override fun onStateChange(state: IntArray): Boolean {
        val color = this.resource.color(state)
        if (this.currentColor != color) {
            this.currentColor = color
            return true
        }
        return false
    }
    
    override fun draw(canvas: Canvas) {
        val paint = getSharedPaint()
        paint.color = this.color
        canvas.drawRect(this.bounds, paint)
    }
}