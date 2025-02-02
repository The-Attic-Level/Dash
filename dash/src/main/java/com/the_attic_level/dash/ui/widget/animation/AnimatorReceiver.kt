package com.the_attic_level.dash.ui.widget.animation

import android.animation.Animator
import android.view.View

open class AnimatorReceiver(val view: View? = null): Animator.AnimatorListener
{
    // ----------------------------------------
    // Animator Listener
    
    override fun onAnimationStart  (animation: Animator) {}
    override fun onAnimationEnd    (animation: Animator) {}
    override fun onAnimationCancel (animation: Animator) {}
    override fun onAnimationRepeat (animation: Animator) {}
}