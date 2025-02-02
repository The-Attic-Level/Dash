package com.the_attic_level.dash.ui.window.sheet

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import com.the_attic_level.dash.app.ternary
import com.the_attic_level.dash.ui.window.DashWindow

class BottomSheetAnimator: DashWindow.Animator
{
    // ----------------------------------------
    // Dash Window Animator
    
    override fun createAnimation(reversed: Boolean): Animation
    {
        val translate = TranslateAnimation(
            Animation.ABSOLUTE, 0.0F,
            Animation.ABSOLUTE, 0.0F,
            Animation.RELATIVE_TO_SELF, ternary(reversed, 0.0F, 0.5F),
            Animation.RELATIVE_TO_SELF, ternary(reversed, 0.5F, 0.0F)
        )
        
        val alpha = AlphaAnimation(
            ternary(reversed, 1.0F, 0.0F),
            ternary(reversed, 0.0F, 1.0F)
        )
        
        val set = AnimationSet(true)
        
        set.addAnimation(translate)
        set.addAnimation(alpha)
        set.interpolator = DecelerateInterpolator()
        set.duration = 192
        
        return set
    }
}