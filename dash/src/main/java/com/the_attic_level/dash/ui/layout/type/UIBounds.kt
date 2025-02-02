package com.the_attic_level.dash.ui.layout.type

import com.the_attic_level.dash.ui.layout.UIMath

class UIBounds
{
    // ----------------------------------------
    // Members (Final)
    
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    
    // ----------------------------------------
    // Properties
    
    val w: Int; get() = this.maxX - this.minX
    val h: Int; get() = this.maxY - this.minY
    
    // ----------------------------------------
    // Methods
    
    fun reset() {
        this.minX = Int.MAX_VALUE
        this.maxX = Int.MIN_VALUE
        this.minY = Int.MAX_VALUE
        this.maxY = Int.MIN_VALUE
    }
    
    fun update(x: Float, y: Float) {
        update(UIMath.round(x), UIMath.round(y))
    }
    
    fun update(x: Int, y: Int) {
        if (x < this.minX) this.minX = x
        if (x > this.maxX) this.maxX = x
        if (y < this.minY) this.minY = y
        if (y > this.maxY) this.maxY = y
    }
}