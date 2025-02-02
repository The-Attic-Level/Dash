package com.the_attic_level.dash.ui.widget.view

import android.content.Context
import android.view.ViewGroup
import android.widget.GridView

open class DashGridView(context: Context) : GridView(context)
{
    // ----------------------------------------
    // Layout Params
    
    override fun checkLayoutParams(params: ViewGroup.LayoutParams?): Boolean {
        return DashLayoutParams.checkLayoutParams(this, params)
    }
    
    override fun generateLayoutParams(params: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return DashLayoutParams.generateAbsListViewParams(params)
    }
}