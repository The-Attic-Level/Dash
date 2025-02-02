package com.the_attic_level.dash.ui.widget.text

import android.text.SpannableString
import android.text.style.CharacterStyle
import com.the_attic_level.dash.app.Dash

class TextBuilder
{
    // ----------------------------------------
    // Class
    
    private class Item {
        var style : CharacterStyle? = null
        var start = 0
        var end   = 0
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private val builder = StringBuilder(128)
    private val stored  = ArrayList<Item>(8)
    private val items   = ArrayList<Item>(8)
    
    // ----------------------------------------
    // Member
    
    private var start = 0
    
    // ----------------------------------------
    // Properties
    
    val length: Int
        get() = this.builder.length
    
    // ----------------------------------------
    // Methods
    
    fun reset()
    {
        for (item in this.items) {
            item.style = null
        }
        
        this.builder.setLength(0)
        this.stored.addAll(this.items)
        this.items.clear()
        this.start = 0
    }
    
    fun setMarker() {
        this.start = this.builder.length
    }
    
    fun append(text: Int) {
        append(Dash.optString(text))
    }
    
    fun append(c: Char) {
        this.builder.append(c)
    }
    
    fun append(text: String?) {
        if (!text.isNullOrEmpty()) {
            this.builder.append(text)
        }
    }
    
    fun append(text: Int, style: CharacterStyle) {
        append(Dash.optString(text), style)
    }
    
    fun append(text: String?, style: CharacterStyle) {
        if (!text.isNullOrEmpty()) {
            val start = this.builder.length
            this.builder.append(text)
            val end = this.builder.length
            append(style, start, end)
        }
    }
    
    fun append(style: CharacterStyle) {
        append(style, this.start, this.builder.length)
    }
    
    fun append(style: CharacterStyle, start: Int, end: Int) {
        val item = this.stored.removeLastOrNull() ?: Item()
        this.items.add(item)
        item.style = style
        item.start = start
        item.end   = end
    }
    
    fun indexOf(str: String): Int {
        return builder.indexOf(str)
    }
    
    fun build(): SpannableString {
        val string = SpannableString(this.builder)
        for (item in this.items) {
            string.setSpan(item.style, item.start, item.end, 0)
        }
        return string
    }
}