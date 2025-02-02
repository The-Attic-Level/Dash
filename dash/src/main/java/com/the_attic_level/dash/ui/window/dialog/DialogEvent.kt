package com.the_attic_level.dash.ui.window.dialog

enum class DialogEvent
{
    // ----------------------------------------
    // Cases
    
    /** The dialog will start the enter animation. */
    ON_SHOW,
    
    /** The dialog is now visible. */
    ON_VISIBLE,
    
    /** The dialog received positive user feedback. */
    ON_POSITIVE,
    
    /** The dialog received neutral user feedback. */
    ON_NEUTRAL,
    
    /** The dialog received negative user feedback. */
    ON_NEGATIVE,
    
    /** The dialog will start the exit animation. */
    ON_HIDE,
    
    /** The dialog is no longer visible. */
    ON_HIDDEN
}