package com.the_attic_level.dash.ui.window.dialog

import com.the_attic_level.dash.ui.window.dialog.DashDialog

interface DialogListener
{
    // ----------------------------------------
    // Methods
    
    /** Notifies about general dialog events. */
    fun onEvent(dialog: DashDialog, event: DialogEvent)
    
    /**
     * Notifies about specific dialog (button) actions. By default the dialog will be
     * dismissed after this method returns. Use 'dialog.keep()' to keep the dialog open.
     */
    fun onAction(dialog: DashDialog, action: DialogAction)
}