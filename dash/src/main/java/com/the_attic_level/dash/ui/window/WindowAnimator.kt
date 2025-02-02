package com.the_attic_level.dash.ui.window

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import com.the_attic_level.dash.app.ternary

class WindowAnimator(
    val scaleXFrom    : Float = 0.9F,
    val scaleYFrom    : Float = 0.9F,
    val scaleFactor   : Float = 1.5F,
    val alphaFactor   : Float = 1.0F,
    val scaleDuration : Long  = 192,
    val alphaDuration : Long  = 192
): DashWindow.Animator
{
    // ----------------------------------------
    // Dash Panel Animator
    
    override fun createAnimation(reversed: Boolean): Animation
    {
        val scale = ScaleAnimation(
            ternary(reversed, 1.0F, this.scaleXFrom),
            ternary(reversed, this.scaleXFrom, 1.0F),
            ternary(reversed, 1.0F, this.scaleYFrom),
            ternary(reversed, this.scaleYFrom, 1.0F),
            ScaleAnimation.RELATIVE_TO_SELF, 0.5F,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5F
        )
        
        scale.duration = this.scaleDuration
        scale.interpolator = DecelerateInterpolator(this.scaleFactor)
        
        val alpha = AlphaAnimation(
            ternary(reversed, 1.0F, 0.0F),
            ternary(reversed, 0.0F, 1.0F)
        )
        
        alpha.duration = this.alphaDuration
        alpha.interpolator = DecelerateInterpolator(this.alphaFactor)
        
        val set = AnimationSet(false)
        set.addAnimation(scale)
        set.addAnimation(alpha)
        
        return set
    }
}