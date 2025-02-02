package com.the_attic_level.dash.ui.layout.type

import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.widget.TextView
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.layout.font.UIFont
import com.the_attic_level.dash.ui.layout.hack.TextCursorHack
import com.the_attic_level.dash.ui.layout.res.UIR

class UIText(
    var font       : UIFont,
    var size       : Int,
    var color      : UIR        = UIR.BLACK,
    var hintColor  : UIR?       = null,
    var background : UIR?       = null,
    var padding    : UIPadding? = null,
    var gravity    : Int        = UI.LEFT_TOP,
    var inputType  : Int        = INPUT_NONE,
    var features   : Int        = FEATURE_NONE)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        // input types
        
        const val INPUT_NONE           = 0
        const val INPUT_NO_SUGGESTIONS = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        const val INPUT_PASSWORD       = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        // features
        
        const val FEATURE_NONE        = 0
        const val FEATURE_SCROLLABLE  = 1
        const val FEATURE_SINGLE_LINE = 2
        const val FEATURE_PASSWORD    = 3
    }
    
    // ----------------------------------------
    // Methods
    
    fun hasFeature(feature: Int): Boolean {
        return this.features and feature != 0
    }
    
    fun apply(view: TextView)
    {
        // text cursor drawable
        TextCursorHack.apply(view)
        
        // apply optional padding
        this.padding?.apply(view)
        
        // apply typeface
        view.typeface = this.font.typeface
        
        // apply optional movement method
        if (hasFeature(FEATURE_SCROLLABLE)) {
            view.movementMethod = ScrollingMovementMethod()
        }
        
        // apply text color
        UI.textColor(view, this.color)
        
        // apply hint color
        this.hintColor?.let {
            UI.hintColor(view, it)
        }
        
        // apply background
        this.background?.let {
            UI.background(view, it)
        }
        
        // set optional single line
        if (hasFeature(FEATURE_SINGLE_LINE)) {
            view.setSingleLine()
        }
        
        // apply text size
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.size.toFloat())
        
        // apply gravity
        view.gravity = this.gravity
        
        val isPassword = hasFeature(FEATURE_PASSWORD)
        
        // apply input type
        if (isPassword) {
            view.inputType = INPUT_PASSWORD
        } else if (this.inputType != 0) {
            view.inputType = this.inputType
        }
        
        if (isPassword) {
            view.transformationMethod = PasswordTransformationMethod()
        }
    }
}