package com.the_attic_level.dash.ui.activity

import com.the_attic_level.dash.app.DashActivity

enum class ActivityEvent
{
    // ----------------------------------------
    // Cases
    
    /** The activity has been created and the UI is available. */
    ON_CREATED,
    
    /** The activity has been started. */
    ON_STARTED,
    
    /** The activity has been resumed. */
    ON_RESUMED,
    
    /** The Activity has been paused. */
    ON_PAUSED,
    
    /** The activity has been stopped. */
    ON_STOPPED,
    
    /** The activity has been destroyed. */
    ON_DESTROYED;
    
    // ----------------------------------------
    // Interface
    
    interface Receiver {
        fun onEvent(activity: DashActivity, event: ActivityEvent)
    }
    
    // ----------------------------------------
    // Class
    
    class Request(val receiver: Receiver, val event: ActivityEvent): DashActivity.Request
    {
        override val type: DashActivity.Request.Type
            get() = DashActivity.Request.Type.EVENT
    }
}