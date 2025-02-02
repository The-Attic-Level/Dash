package com.the_attic_level.dash.ui.layout.res

import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

abstract class UIDrawable: Drawable(), UIR
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        private val PAINT = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        
        fun getSharedPaint(): Paint
        {
            PAINT.style       = Paint.Style.FILL
            PAINT.colorFilter = null
            PAINT.xfermode    = null
            PAINT.shader      = null
            
            return PAINT
        }
    }
    
    // ----------------------------------------
    // UIR
    
    override val type: UIR.Type
        get() = UIR.Type.DRAWABLE
    
    override val drawable: Drawable?
        get() = create()
    
    // ----------------------------------------
    // Drawable
    
    override fun setAlpha(alpha: Int) {
        // not used
    }
    
    override fun setColorFilter(filter: ColorFilter?) {
        // not used
    }
    
    @Deprecated("deprecated by android", ReplaceWith("nothing"))
    override fun getOpacity() = PixelFormat.OPAQUE
    
    // ----------------------------------------
    // Methods (Protected)
    
    protected abstract fun create() : UIDrawable
}