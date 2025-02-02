package com.the_attic_level.dash.ui.activity

import androidx.core.graphics.Insets
import com.the_attic_level.dash.app.DashActivity

class InsetRequest(val receiver: Receiver) : DashActivity.Request
{
    // ----------------------------------------
    // Interface
    
    interface Receiver {
        fun onInsets(systemBars: Insets)
    }
    
    // ----------------------------------------
    // Dash Activity Request
    
    override val type: DashActivity.Request.Type
        get() = DashActivity.Request.Type.INSETS
}