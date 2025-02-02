package com.the_attic_level.dash.ui.window

import android.content.Context
import android.view.KeyEvent
import android.widget.FrameLayout

class WindowView(context: Context): FrameLayout(context)
{
    // ----------------------------------------
    // Members (Final)
    
    internal var onBackPressed  : (() -> Unit)? = null
    internal var onOutsideClick : (() -> Unit)? = null
    
    // ----------------------------------------
    // Init
    
    init {
        setOnClickListener {
            this.onOutsideClick?.invoke()
        }
    }
    
    // ----------------------------------------
    // Methods
    
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            this.onBackPressed?.invoke()
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}