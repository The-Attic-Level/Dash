package com.the_attic_level.dash.ui.activity

enum class LifecycleEvent(
    val activityEvent: ActivityEvent? = null,
    val activityState: ActivityState? = null
){
    // ----------------------------------------
    // Cases
    
    ON_PRE_CREATE,
    ON_PRE_START,
    ON_PRE_RESUME,
    ON_PRE_PAUSE,
    ON_PRE_STOP,
    ON_PRE_DESTROYED,
    
    ON_POST_CREATE    (ActivityEvent.ON_CREATED,   ActivityState.CREATED),
    ON_POST_START     (ActivityEvent.ON_STARTED,   ActivityState.STARTED),
    ON_POST_RESUME    (ActivityEvent.ON_RESUMED,   ActivityState.RESUMED),
    ON_POST_PAUSE     (ActivityEvent.ON_PAUSED,    ActivityState.PAUSED),
    ON_POST_STOP      (ActivityEvent.ON_STOPPED,   ActivityState.STOPPED),
    ON_POST_DESTROYED (ActivityEvent.ON_DESTROYED, ActivityState.DESTROYED);
}