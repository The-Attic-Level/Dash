package com.the_attic_level.dash.ui.layout.font

import android.graphics.Typeface

interface UIFont
{
    // ----------------------------------------
    // Enum
    
    enum class Family
    {
        DEFAULT, DEFAULT_BOLD, SANS_SERIF, MONOSPACE, SERIF;
        
        val typeface: Typeface
            get() = when(this) {
                DEFAULT      -> Typeface.DEFAULT
                DEFAULT_BOLD -> Typeface.DEFAULT_BOLD
                SANS_SERIF   -> Typeface.SANS_SERIF
                MONOSPACE    -> Typeface.MONOSPACE
                SERIF        -> Typeface.SERIF
            }
    }
    
    enum class Weight(val value: Int)
    {
        THIN        (100),
        EXTRA_LIGHT (200),
        LIGHT       (300),
        NORMAL      (400),
        MEDIUM      (500),
        SEMI_BOLD   (600),
        BOLD        (700),
        EXTRA_BOLD  (800),
        BLACK       (900)
    }
    
    enum class Style(val value: Int)
    {
        NORMAL      (Typeface.NORMAL),
        BOLD        (Typeface.BOLD),
        ITALIC      (Typeface.ITALIC),
        BOLD_ITALIC (Typeface.BOLD_ITALIC)
    }
    
    // ----------------------------------------
    // Static
    
    companion object {
        var SYSTEM_NORMAL = UISystemFont(Family.DEFAULT, Style.NORMAL)
        var SYSTEM_BOLD   = UISystemFont(Family.DEFAULT, Style.BOLD)
    }
    
    // ----------------------------------------
    // Methods
    
    val typeface: Typeface?
}