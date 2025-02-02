package com.the_attic_level.dash.ui.activity

import android.app.Activity
import android.content.Intent
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.DashActivity

class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)
{
    // ----------------------------------------
    // Interface
    
    interface Receiver {
        fun onResult(activity: DashActivity, result: ActivityResult)
    }
    
    // ----------------------------------------
    // Class
    
    class Request(receiver: Receiver): DashActivity.Request
    {
        override val type: DashActivity.Request.Type
            get() = DashActivity.Request.Type.RESULT
        
        val requestCode = Dash.nextUniqueInt()
        val receiver : Receiver
        
        init {
            this.receiver = receiver
        }
        
        fun notify(activity: DashActivity, requestCode: Int, resultCode: Int, data: Intent?) : Boolean {
            if (this.requestCode == requestCode) {
                this.receiver.onResult(activity, ActivityResult(requestCode, resultCode, data))
                return true
            }
            return false
        }
    }
    
    // ----------------------------------------
    // Properties
    
    val isSuccessful: Boolean; get() {
        return this.resultCode == Activity.RESULT_OK
    }
}