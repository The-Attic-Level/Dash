package com.the_attic_level.dash.ui.layout.res

import android.content.res.ColorStateList

class UIColorList(disabled: UIR, enabled: UIR, pressed: UIR, selected: UIR) : ColorStateList(STATES, COLORS), UIR
{
    // ----------------------------------------
    // Constants
    
    companion object
    {
        // ------------------------------------
        // Private
        
        private val STATES : Array<IntArray> = arrayOf(intArrayOf(0))
        private val COLORS : IntArray = intArrayOf(0)
        
        private const val UNDEFINED  : Byte = 0
        private const val NOT_OPAQUE : Byte = 1
        private const val OPAQUE     : Byte = 2
        
        private fun isOpaque(colors: Array<UIR>) : Boolean {
            for (res in colors) {
                if (res.color ushr 24 != 255) {
                    return false
                }
            }
            return true
        }
        
        // ------------------------------------
        // Public
        
        const val INDEX_DISABLED = 0
        const val INDEX_ENABLED  = 1
        const val INDEX_PRESSED  = 2
        const val INDEX_SELECTED = 3
        
        const val STATE_ENABLED  = android.R.attr.state_enabled
        const val STATE_PRESSED  = android.R.attr.state_pressed
        const val STATE_SELECTED = android.R.attr.state_selected
        
        // fun isEnabled()
        
        fun isEnabled (states: IntArray) = states.contains(STATE_ENABLED)
        fun isPressed (states: IntArray) = states.contains(STATE_PRESSED)
        fun isSelected(states: IntArray) = states.contains(STATE_SELECTED)
        
        fun describe(states: IntArray?): String
        {
            var result = ""
            
            if (states != null && states.isNotEmpty())
            {
                for (state in states)
                {
                    if (result.isNotEmpty()) {
                        result += ", "
                    }
                    
                    result += when (state) {
                        STATE_ENABLED  -> "enabled"
                        STATE_PRESSED  -> "pressed"
                        STATE_SELECTED -> "selected"
                        else           -> "$state"
                    }
                }
            }
            
            if (result.isEmpty()) {
                result += "disabled"
            }
            
            return "[$result]"
        }
    }
    
    // ----------------------------------------
    // Members
    
    private var colors : Array<UIR>
    private var opaque = UNDEFINED
    
    // ----------------------------------------
    // Properties
    
    val disabled : UIR; get() = this.colors[INDEX_DISABLED]
    val enabled  : UIR; get() = this.colors[INDEX_ENABLED]
    val pressed  : UIR; get() = this.colors[INDEX_PRESSED]
    val selected : UIR; get() = this.colors[INDEX_SELECTED]
    
    val description: String; get() {
        return "[disabled: ${this.disabled.color}, " +
                 "enabled: ${this.enabled.color}, "  +
                 "pressed: ${this.pressed.color}, "  +
                "selected: ${this.selected.color}]"
    }
    
    // ----------------------------------------
    // Init
    
    constructor(disabled : UIR, enabled : UIR, pressed: UIR) : this(disabled, enabled, pressed, pressed)
    constructor(enabled  : UIR, pressed : UIR)               : this(enabled,  enabled, pressed, pressed)
    
    init {
        this.colors = arrayOf(disabled, enabled, pressed, selected)
    }
    
    // ----------------------------------------
    // Methods
    
    fun get(enabled: Boolean = true, pressed: Boolean, selected: Boolean = false) : Int {
        if (selected) {
            return this.colors[INDEX_SELECTED].color
        } else if (pressed) {
            return this.colors[INDEX_PRESSED].color
        } else if (enabled) {
            return this.colors[INDEX_ENABLED].color
        }
        return this.colors[INDEX_DISABLED].color
    }
    
    fun get(states: IntArray?): Int {
        return getColorForState(states, 0)
    }
    
    fun set(index: Int, color: UIR) {
        this.colors[index] = color
        this.opaque = UNDEFINED
    }
    
    // ----------------------------------------
    // Color State List
    
    override fun isStateful() = true
    
    override fun isOpaque(): Boolean {
        if (this.opaque == UNDEFINED) {
            this.opaque = if (isOpaque(this.colors)) OPAQUE else NOT_OPAQUE
        }
        return this.opaque == OPAQUE
    }
    
    override fun getColorForState(states: IntArray?, ignore: Int): Int
    {
        var index = INDEX_DISABLED
        
        if (states != null) {
            for (state in states) {
                if (state == STATE_ENABLED && index < INDEX_ENABLED) {
                    index = INDEX_ENABLED
                } else if (state == STATE_PRESSED) {
                    index = INDEX_PRESSED
                } else if (state == STATE_SELECTED && index != INDEX_PRESSED) {
                    index = INDEX_SELECTED
                }
            }
        }
        
        return this.colors[index].color
    }
    
    override fun getDefaultColor() = this.enabled.color
    
    // ----------------------------------------
    // UIR
    
    override val type: UIR.Type
        get() = UIR.Type.COLOR_LIST
    
    override val color: Int
        get() = this.enabled.color
    
    override val list: ColorStateList
        get() = this
}