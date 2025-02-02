package com.the_attic_level.dash.ui.widget.text

import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.StyleSpan
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.ui.layout.font.UIFont
import com.the_attic_level.dash.ui.layout.res.UIR

class TextSpan(
        style : Int     = NORMAL,
    val font  : UIFont? = null,
    val color : Int     = 0,
    val scale : Float   = 0.0F
): StyleSpan(style)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        const val NORMAL      = Typeface.NORMAL
        const val BOLD        = Typeface.BOLD
        const val ITALIC      = Typeface.ITALIC
        const val BOLD_ITALIC = Typeface.BOLD_ITALIC
        const val UNDERLINE   = 4
        
        private const val TYPEFACE_MASK = 0x03
    }
    
    // ----------------------------------------
    // Init
    
    constructor(style: Int): this(style=style, font=null, color=0, scale=0.0F)
    
    constructor(style: Int = NORMAL, font: UIFont? = null, color: UIR? = null, scale: Float = 0.0F):
            this(style, font, color?.color ?: 0, scale)
    
    // ----------------------------------------
    // Style Span
    
    override fun updateDrawState(paint: TextPaint)
    {
        if (this.font != null) {
            paint.typeface = font.typeface
        }
        
        if (this.style and TYPEFACE_MASK != 0) {
            super.updateDrawState(paint)
        }
        
        if (this.scale != 0.0F) {
            paint.textSize *= this.scale
        }
        
        if (this.color != 0) {
            paint.color = this.color
        }
        
        if (this.style and UNDERLINE != 0) {
            paint.isUnderlineText = true
        }
    }
    
    override fun updateMeasureState(paint: TextPaint)
    {
        if (this.font != null) {
            paint.typeface = this.font.typeface
        }
        
        if (this.style and TYPEFACE_MASK != 0) {
            super.updateMeasureState(paint)
        }
        
        if (this.scale != 0.0F) {
            paint.textSize *= this.scale
        }
    }
    
    // ----------------------------------------
    // Build
    
    fun build(text: Int): SpannableString {
        return build(Dash.string(text))
    }
    
    fun build(text: String): SpannableString {
        val string = SpannableString(text)
        string.setSpan(this, 0, text.length, 0)
        return string
    }
}