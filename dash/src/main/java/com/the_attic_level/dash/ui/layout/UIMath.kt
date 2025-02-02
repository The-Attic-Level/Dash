package com.the_attic_level.dash.ui.layout

import android.graphics.PointF
import kotlin.math.atan2
import kotlin.math.sqrt

class UIMath
{
    companion object
    {
        // ----------------------------------------
        // Clamp
        
        fun min(a: Float, b: Float): Float =
            kotlin.math.min(a, b)
        
        fun max(a: Float, b: Float): Float =
            kotlin.math.max(a, b)
        
        fun clamp(value: Float, min: Float, max: Float): Float =
            if (value < min) min else kotlin.math.min(value, max)
        
        fun clamp(value: Int, min: Int, max: Int): Int =
            if (value < min) min else kotlin.math.min(value, max)
        
        // ----------------------------------------
        // Floor / Even
        
        fun floor(value: Int, scale: Float): Int =
            (value.toFloat() * scale).toInt()
        
        fun floor(value: Float, scale: Float): Int =
            (value * scale).toInt()
        
        fun even(value: Int, scale: Float): Int =
            even((value.toFloat() * scale).toInt())
        
        fun even(value: Int): Int =
            if (value and 0x1 == 1) value + 1 else value
        
        fun evenDown(value: Int): Int =
            if (value and 0x1 == 1) value - 1 else value
        
        fun even(value: Float, scale: Float): Int =
            even((value * scale).toInt())
        
        // ----------------------------------------
        // Round
        
        fun round(value: Int, scale: Float): Int =
            round(value.toFloat() * scale)
        
        fun round(value: Float, scale: Float): Int =
            round(value * scale)
        
        fun round(value: Float): Int {
            if (value > 0.0F) {
                return (value + 0.5F).toInt()
            } else if (value < 0.0F) {
                return (value - 0.5F).toInt()
            }
            return 0
        }
        
        fun round(value: Double): Int {
            if (value > 0.0) {
                return (value + 0.5).toInt()
            } else if (value < 0.0) {
                return (value - 0.5).toInt()
            }
            return 0
        }
        
        // ----------------------------------------
        // Methods
        
        fun distance(ax: Float, ay: Float, bx: Float, by: Float): Float =
            length(bx - ax, by - ay)
        
        fun distance(a: PointF, b: PointF): Float =
            length(b.x - a.x, b.y - a.y)
        
        fun length(a: Float, b: Float): Float =
            sqrt((a * a + b * b).toDouble()).toFloat()
        
        // ----------------------------------------
        // Scale
        
        fun scale(src: Float, min: Float, max: Float): Float =
            (src - min) / (max - min)
        
        fun scale(src: Float, srcMin: Float, srcMax: Float, dstMin: Float, dstMax: Float): Float =
            dstMin + (src - srcMin) / (srcMax - srcMin) * (dstMax - dstMin)
        
        // ----------------------------------------
        // Other
        
        fun angle(a: PointF, b: PointF): Double =
            atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())
        
        fun angle(ax: Float, ay: Float, bx: Float, by: Float): Double =
            atan2((by - ay).toDouble(), (bx - ax).toDouble())
        
        fun fastSqrt(x: Float): Float
        {
            val xHalf = 0.5F * x
            var i = java.lang.Float.floatToIntBits(x)
            i = 0x5F3759DF - (i shr 1)
            var t = java.lang.Float.intBitsToFloat(i)
            t *= 1.5F - xHalf * t * t
            
            // resolve inversion
            return if (t != 0.0F) 1.0F / t else 0.0F
        }
    }
}