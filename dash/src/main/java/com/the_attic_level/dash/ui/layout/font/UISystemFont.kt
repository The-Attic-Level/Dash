package com.the_attic_level.dash.ui.layout.font

import android.graphics.Typeface
import android.os.Build
import com.the_attic_level.dash.sys.Logger

class UISystemFont private constructor(
    val family : UIFont.Family?,
    val name   : String?,
    val style  : UIFont.Style?,
    val weight : UIFont.Weight?,
    val italic : Boolean
): UIFont
{
    // ----------------------------------------
    // Members
    
    private var _typeface: Typeface? = null
    private var initialized = false
    
    // ----------------------------------------
    // Init
    
    constructor(family: UIFont.Family, weight: UIFont.Weight, italic: Boolean = false):
            this(family, null, null, weight, italic)
    
    constructor(family: UIFont.Family, style: UIFont.Style):
            this(family, null, style, null, false)
    
    constructor(name: String, style: UIFont.Style):
            this(null, name, style, null, false)
    
    // ----------------------------------------
    // UI Font
    
    override val typeface: Typeface?; get()
    {
        if (!this.initialized)
        {
            this.initialized = true
            
            if (this.family != null) {
                if (this.weight != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        this._typeface = Typeface.create(this.family.typeface, this.weight.value, this.italic)
                    }
                } else if (this.style != null) {
                    this._typeface = Typeface.create(this.family.typeface, this.style.value);
                }
            } else if (this.name != null && this.style != null) {
                this._typeface = Typeface.create(this.name, this.style.value)
            }
            
            if (this._typeface == null) {
                Logger.error(this, "unable to load typeface")
            }
        }
        
        return this._typeface
    }
}