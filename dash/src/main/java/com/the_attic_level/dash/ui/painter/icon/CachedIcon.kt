package com.the_attic_level.dash.ui.painter.icon

import android.graphics.Bitmap
import android.graphics.Color
import com.the_attic_level.dash.ui.painter.UIPainter

abstract class CachedIcon: Icon
{
    // ----------------------------------------
    // Class
    
    private class Item(val bitmap: Bitmap, val size: Int)
    
    // ----------------------------------------
    // Static
    
    companion object {
        var DRAW_DEBUG_FRAME = false
    }
    
    // ----------------------------------------
    // Members
    
    private val params = Icon.Params()
    private val items = ArrayList<Item>(4)
    private var recent: Item? = null
    
    // ----------------------------------------
    // Icon
    
    override fun getBitmap(size: Int): Bitmap?
    {
        if (size <= 0) {
            return null
        }
        
        // check if bitmaps already exists for given size
        
        var recent = this.recent
        
        if (recent != null && recent.size == size) {
            return recent.bitmap
        }
        
        for (item in this.items) {
            if (item.size == size) {
                this.recent = item
                return item.bitmap
            }
        }
        
        // create bitmap for given size
        
        val bitmap = createBitmap(size)
        
        if (bitmap != null) {
            recent = Item(bitmap, size)
            this.items.add(recent)
            this.recent = recent
        }
        
        return bitmap
    }
    
    // ----------------------------------------
    // Methods (Abstract)
    
    /** Calculates the icon boundaries for a given size. */
    protected abstract fun onMeasure(params: Icon.Params)
    
    /** Draws the icon onto the given painter. */
    protected abstract fun onDraw(painter: UIPainter, params: Icon.Params)
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun createBitmap(size: Int): Bitmap?
    {
        this.params.reset()
        this.params.size = size
        
        onMeasure(this.params)
        
        if (this.params.w > 0 && this.params.h > 0)
        {
            return UIPainter.create(this.params.w, this.params.h, Bitmap.Config.ARGB_8888)
            {
                painter ->
                
                if (DRAW_DEBUG_FRAME) {
                    val color = painter.color
                    painter.color = Color.WHITE
                    painter.frame(this.params, 1)
                    painter.color = color
                }
                
                onDraw(painter, this.params)
            }
        }
        
        return null
    }
}