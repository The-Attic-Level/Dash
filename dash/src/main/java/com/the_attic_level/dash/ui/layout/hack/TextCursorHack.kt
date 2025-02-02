package com.the_attic_level.dash.ui.layout.hack

import android.annotation.SuppressLint
import android.os.Build
import android.widget.TextView
import com.the_attic_level.dash.sys.Logger
import java.lang.reflect.Field

class TextCursorHack
{
    @SuppressLint("PrivateApi")
    companion object
    {
        // ----------------------------------------
        // Members (Private)
        
        private var field: Field? = null
        
        // ----------------------------------------
        // Init
        
        init {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                try {
                    this.field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                    this.field?.isAccessible = true
                } catch (ignore: Exception) {}
            }
        }
        
        // ----------------------------------------
        // Methods
        
        fun apply(view: TextView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.setTextCursorDrawable(0)
            } else {
                try {
                    this.field?.set(view, 0)
                } catch (e: IllegalAccessException) {
                    e.message?.let { Logger.warn(TextCursorHack::class, it) }
                }
            }
        }
    }
}