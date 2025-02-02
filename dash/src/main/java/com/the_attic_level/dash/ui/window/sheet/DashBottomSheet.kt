package com.the_attic_level.dash.ui.window.sheet

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.window.DashWindow
import com.the_attic_level.dash.ui.window.WindowParams

typealias BottomSheetStyle = DashBottomSheet.Style

abstract class DashBottomSheet(
    activity: Activity,
    override val style: BottomSheetStyle = UIStyle.shared.bottomSheet
): DashWindow(activity, style)
{
    // ----------------------------------------
    // Interface
    
    interface Style: DashWindow.Style {
        override fun createParams(): FrameLayout.LayoutParams {
            return FrameLayout.LayoutParams(this.panelWidth, this.panelHeight, UI.BOTTOM)
        }
    }
    
    // ----------------------------------------
    // Dash Window
    
    override fun onCreateWindowParams(): WindowParams
    {
        val params = WindowParams(
            UI.MATCH_PARENT, UI.MATCH_PARENT,
            WindowParams.TYPE_APPLICATION,
            WindowParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowParams.FLAG_HARDWARE_ACCELERATED or
                    WindowParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT)
        
        // the window needs to cover the navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            params.fitInsetsTypes = WindowInsets.Type.navigationBars()
            params.fitInsetsSides = WindowInsets.Side.TOP
        } else {
            @Suppress("DEPRECATION")
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        
        return params
    }
}