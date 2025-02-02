package com.the_attic_level.dash.ui.widget.view

import android.content.Context
import android.text.Layout
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.ceil
import kotlin.math.max

class DashTextView(context: Context): AppCompatTextView(context)
{
    // ----------------------------------------
    // Members
    
    private var horizontalContentWrapping = false
    
    // ----------------------------------------
    // Methods
    
    fun enableHorizontalContentWrapping(enabled: Boolean = true) {
        this.horizontalContentWrapping = enabled
    }
    
    override fun onMeasure(wSpec: Int, hSpec: Int)
    {
        super.onMeasure(wSpec, hSpec)
        
        if (this.horizontalContentWrapping) {
            computeHorizontalContentWrapping()
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private fun computeHorizontalContentWrapping()
    {
        val layout = this.layout
        
        if (layout == null || layout.lineCount < 2) {
            return
        }
        
        val maxLineWidth = ceil(getMaxLineWidth(layout).toDouble()).toInt()
        val uselessPaddingWidth = layout.width - maxLineWidth
        
        val width  = this.measuredWidth - uselessPaddingWidth
        val height = this.measuredHeight
        
        setMeasuredDimension(width, height)
    }
    
    private fun getMaxLineWidth(layout: Layout): Float {
        var max = 0.0F
        for (i in 0 until layout.lineCount) {
            max = max(max.toDouble(), layout.getLineWidth(i).toDouble()).toFloat()
        }
        return max
    }
}