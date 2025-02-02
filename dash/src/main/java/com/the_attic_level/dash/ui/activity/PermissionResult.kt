package com.the_attic_level.dash.ui.activity

import android.content.pm.PackageManager
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.DashActivity

class PermissionResult(val requestCode: Int, val permissions: Array<String>, val results: IntArray)
{
    // ----------------------------------------
    // Interface
    
    interface Receiver {
        fun onResult(activity: DashActivity, result: PermissionResult)
    }
    
    // ----------------------------------------
    // Class
    
    class Request(val receiver: Receiver): DashActivity.Request
    {
        override val type: DashActivity.Request.Type
            get() = DashActivity.Request.Type.PERMISSION
        
        val requestCode: Int = Dash.nextUniqueInt()
        
        fun notify(activity: DashActivity, requestCode: Int, permissions: Array<String>, results: IntArray): Boolean {
            if (this.requestCode == requestCode) {
                this.receiver.onResult(activity, PermissionResult(requestCode, permissions, results))
                return true
            }
            return false
        }
    }
    
    // ----------------------------------------
    // Properties
    
    val granted: Boolean; get() {
        for (result in this.results) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
    
    // ----------------------------------------
    // Methods
    
    fun isGranted(permission: String) : Boolean {
        for (i in this.permissions.indices) {
            if (permission == this.permissions[i]) {
                return results[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }
}