package com.the_attic_level.dash.ui.painter.shape

import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.widget.drawable.ShapeDrawable

interface UIShape
{
    // ----------------------------------------
    // Methods
    
    fun draw(painter: UIPainter, box: UIBox)
    
    // ----------------------------------------
    // Methods (Default)
    
    fun drawable(color: UIR) = ShapeDrawable(color, this)
}