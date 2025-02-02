package com.the_attic_level.dash.ui.window.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.the_attic_level.dash.ui.layout.res.UIColor
import com.the_attic_level.dash.ui.layout.res.UIDrawable

class OverlayBackground(private val onFinished: (Boolean) -> Unit): UIDrawable()
{
    // ----------------------------------------
    // Members
    
    private var interpolator: Interpolator = LinearInterpolator()
    
    private var active       = false
    private var duration     = 0L
    private var sourceTime   = 0L
    
    private var sourceColor  = Color.TRANSPARENT
    private var targetColor  = Color.TRANSPARENT
    private var currentColor = Color.TRANSPARENT
    
    // ----------------------------------------
    // Methods
    
    fun start(color: Int, duration: Long, interpolator: Interpolator)
    {
        if (duration <= 0)
        {
            this.currentColor = color
            this.active = false
            
            val visible = Color.alpha(this.currentColor) > 0
            this.onFinished.invoke(visible)
        }
        else
        {
            this.interpolator = interpolator
            this.sourceColor  = this.currentColor
            this.targetColor  = color
            this.sourceTime   = 0L
            this.duration     = duration
            this.active       = true
        }
        
        invalidateSelf()
    }
    
    // ----------------------------------------
    // Drawable
    
    override fun draw(canvas: Canvas)
    {
        val currentTime = SystemClock.elapsedRealtime()
        
        if (this.active)
        {
            if (this.sourceTime == 0L)
            {
                this.sourceTime = currentTime
                invalidateSelf()
            }
            else
            {
                val elapsed = currentTime - this.sourceTime
                
                if (elapsed < this.duration)
                {
                    val scale = elapsed.toFloat() / this.duration.toFloat()
                    
                    if (scale > 0.0F && scale < 1.0F) {
                        val interpolated = this.interpolator.getInterpolation(scale)
                        this.currentColor = UIColor.lerp(this.sourceColor, this.targetColor, interpolated)
                    } else if (scale >= 1.0F) {
                        this.currentColor = this.targetColor
                    }
                    
                    invalidateSelf()
                }
                else
                {
                    this.currentColor = this.targetColor
                    this.active = false
                    
                    val visible = Color.alpha(this.currentColor) > 0
                    this.onFinished.invoke(visible)
                }
            }
        }
        
        val paint = getSharedPaint()
        paint.color = this.currentColor
        canvas.drawRect(this.bounds, paint)
    }
    
    override fun create(): UIDrawable {
        throw Exception("invalid operation")
    }
}