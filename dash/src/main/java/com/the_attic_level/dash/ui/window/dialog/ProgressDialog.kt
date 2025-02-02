package com.the_attic_level.dash.ui.window.dialog

import android.app.Activity
import android.view.Gravity
import android.widget.LinearLayout
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.layout.type.UIRect

class ProgressDialog(
    activity: Activity,
    style: DialogStyle = UIStyle.shared.dialog
): DashDialog(activity, style)
{
    // ----------------------------------------
    // Dash Dialog
    
    override fun onCreateContent(content: LinearLayout)
    {
        // create wrapper layout
        
        val wrapper = UI.horizontal(this.activity, UI.MATCH_WRAP)
        wrapper.gravity = Gravity.CENTER_VERTICAL
        this.style.text.padding?.apply(wrapper)
        content.addView(wrapper)
        
        // create progress bar
        
        val rect = UIRect.fromSize(this.style.progressWheelSize)
        val bar = UI.progress(this.activity, rect)
        wrapper.addView(bar)
        
        // create text view
        
        this.textView = createTextView()
        this.textView?.let {
            val l = it.paddingLeft
            val t = it.paddingTop
            val b = it.paddingBottom
            it.setPadding(l, t, 0, b)
        }
        
        wrapper.addView(this.textView)
    }
}