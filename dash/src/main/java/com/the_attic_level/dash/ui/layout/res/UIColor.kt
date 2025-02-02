package com.the_attic_level.dash.ui.layout.res

import android.graphics.Color
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.ui.layout.UIMath

class UIColor(private val value: Int): UIR
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        fun hex(color: Int): String {
            return String.format("#%06X", 0xFFFFFF and color)
        }
        
        fun lerp(a: Int, b: Int, @FloatRange(from = 0.0, to = 1.0) scale: Float): Int
        {
            val a1 = (a ushr 24)         .toFloat()
            val r1 = (a shr  16 and 0xFF).toFloat()
            val g1 = (a shr   8 and 0xFF).toFloat()
            val b1 = (a         and 0xFF).toFloat()
            
            val a2 = (b ushr 24)         .toFloat()
            val r2 = (b shr  16 and 0xFF).toFloat()
            val g2 = (b shr   8 and 0xFF).toFloat()
            val b2 = (b         and 0xFF).toFloat()
            
            val a3 = UIMath.clamp((a1 + (a2 - a1) * scale + 0.5F).toInt(), 0, 255)
            val r3 = UIMath.clamp((r1 + (r2 - r1) * scale + 0.5F).toInt(), 0, 255)
            val g3 = UIMath.clamp((g1 + (g2 - g1) * scale + 0.5F).toInt(), 0, 255)
            val b3 = UIMath.clamp((b1 + (b2 - b1) * scale + 0.5F).toInt(), 0, 255)
            
            // alpha | red | green | blue
            return a3 shl 24 or (r3 shl 16) or (g3 shl 8) or b3
        }
        
        fun hsv(h: Int, s: Float, v: Float, alpha: Int=0xFF) =
            Color.HSVToColor(alpha, floatArrayOf(h.toFloat(), s, v))
        
        fun applyAlpha(color: Int, @IntRange(from = 0, to = 255) alpha: Int): Int {
            return color and 0x00FFFFFF or (alpha shl 24)
        }
        
        fun applyAlpha(color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
            return applyAlpha(color, UIMath.clamp(UIMath.round(alpha * 255.0F), 0, 255))
        }
        
        fun fromResource(id: Int): UIColor {
            return UIColor(Dash.color(id))
        }
    }
    
    // ----------------------------------------
    // UI Resource
    
    override val type
        get() = UIR.Type.COLOR
    
    override val color: Int
        get() = this.value
    
    override fun color(state: IntArray): Int = this.value
}