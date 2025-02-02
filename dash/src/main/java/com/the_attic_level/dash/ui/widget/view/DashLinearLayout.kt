package com.the_attic_level.dash.ui.widget.view

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout

open class DashLinearLayout(context: Context) : LinearLayout(context)
{
    // ----------------------------------------
    // Layout Params
    
    override fun checkLayoutParams(params: ViewGroup.LayoutParams?): Boolean {
        return DashLayoutParams.checkLayoutParams(this, params)
    }
    
    override fun generateLayoutParams(params: ViewGroup.LayoutParams?): LayoutParams {
        return DashLayoutParams.generateLinearLayoutParams(params)
    }
}