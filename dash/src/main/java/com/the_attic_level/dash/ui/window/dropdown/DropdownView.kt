package com.the_attic_level.dash.ui.window.dropdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.Gravity
import android.widget.TextView
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.painter.UIPainter
import com.the_attic_level.dash.ui.painter.icon.Icon

@SuppressLint("AppCompatCustomView", "ViewConstructor")
class DropdownView(context: Context, val style: DropdownStyle): TextView(context)
{
    // ----------------------------------------
    // Members
    
    private var icon  : Icon? = null
    private var scale : Float = 0.0F
    
    // ----------------------------------------
    // Factory
    
    companion object
    {
        fun create(context: Context, style: DropdownStyle): DropdownView
        {
            val view = DropdownView(context, style)
            UI.setup(view, UI.MATCH_WRAP, style.entryTitle)
            view.minHeight = style.minEntryHeight
            
            return view
        }
    }
    
    // ----------------------------------------
    // Methods
    
    fun setup(entry: DashDropdown.Entry) {
        this.text  = entry.dropdownValue
        this.icon  = entry.dropdownIcon
        this.scale = entry.dropdownIconScale
    }
    
    // ----------------------------------------
    // Text View
    
    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)
        
        val icon = this.icon
        
        if (icon != null)
        {
            UIPainter.draw(canvas, this)
            {
                var size  = this.style.iconSize
                val space = this.style.iconSpace
                
                if (size.toFloat() != 1.0F) {
                    size = UIMath.even(size, this.scale)
                }
                
                it.gravity = Gravity.CENTER
                it.color(this.style.iconColor)
                it.draw(icon, this.width - space, 0, space, this.height, size)
            }
        }
    }
}