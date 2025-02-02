package com.the_attic_level.dash.ui.layout.font

import android.graphics.Typeface
import com.the_attic_level.dash.app.DashApp
import com.the_attic_level.dash.sys.Logger

class UIAssetFont(val path: String): UIFont
{
    // ----------------------------------------
    // Members
    
    private var _typeface: Typeface? = null
    private var initialized = false
    
    // ----------------------------------------
    // UI Font
    
    override val typeface: Typeface?; get()
    {
        if (!this.initialized)
        {
            this.initialized = true
            
            try {
                this._typeface = Typeface.createFromAsset(DashApp.shared.assets, this.path)
            } catch (e: Exception) {
                Logger.error(this, "unable to load font '${this.path}': ${e.message}")
            }
        }
        
        return this._typeface
    }
}