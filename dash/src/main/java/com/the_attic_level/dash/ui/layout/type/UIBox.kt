package com.the_attic_level.dash.ui.layout.type

import android.view.MotionEvent

open class UIBox(var x: Int=0, var y: Int=0, var w: Int=0, var h: Int=0)
{
    // ----------------------------------------
    // Properties
    
    val cx get() = this.x + this.w / 2
    val cy get() = this.y + this.h / 2
    
    // ----------------------------------------
    // Methods
    
    fun set(box: UIBox) {
        this.x = box.x
        this.y = box.y
        this.w = box.w
        this.h = box.h
    }
    
    fun set(x: Int, y: Int, w: Int, h: Int) {
        this.x = x
        this.y = y
        this.w = w
        this.h = h
    }
    
    fun inset(margin: Int) {
        this.x += margin
        this.y += margin
        this.w -= margin * 2
        this.h -= margin * 2
    }
    
    fun reset() {
        this.x = 0
        this.y = 0
        this.w = 0
        this.h = 0
    }
    
    fun contains(event: MotionEvent): Boolean {
        return contains(event.x.toInt(), event.y.toInt())
    }
    
    fun contains(x: Float, y: Float): Boolean {
        return contains(x.toInt(), y.toInt())
    }
    
    fun contains(x: Int, y: Int): Boolean {
        return x >= this.x && x <= this.x + this.w &&
               y >= this.y && y <= this.y + this.h
    }
    
    // ----------------------------------------
    // Object
    
    override fun toString(): String {
        return "[x: ${this.x}, y: ${this.y}, w: ${this.w}, h: ${this.h}]"
    }
}