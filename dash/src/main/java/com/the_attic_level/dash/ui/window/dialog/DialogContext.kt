package com.the_attic_level.dash.ui.window.dialog

import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UIStyle

private typealias DialogEventReceiver  = (DashDialog, DialogEvent)  -> Unit
private typealias DialogActionReceiver = (DashDialog, DialogAction) -> Unit

interface DialogContext
{
    // ----------------------------------------
    // Properties
    
    /** Provides the dialog title. */
    val title: CharSequence?
    
    /** Provides the dialog message. */
    val text: CharSequence?
    
    /** Provides the dialog button types. */
    val actions: Array<DialogAction>
    
    // ----------------------------------------
    // Methods (Default)
    
    /** Provides the Window Style. */
    val style: DialogStyle
        get() = UIStyle.shared.dialog
    
    /** Provides the dialog class. */
    val cls: DialogClass
        get() = DashDialog.defaultDialogClass
    
    /** Whether the dialog can be dismissed using the back button or by clicking outside the window. */
    val allowsImplicitCancellation: Boolean
        get() = true
    
    /** Provides an optional tag object that contains additional context information. */
    var tag: Any?
    
    // ----------------------------------------
    // Properties
    
    val dialog: DashDialog?; get() {
        val activity = Dash.currentActivity as? DashActivity
        if (activity != null) {
            return activity.findActiveDialog(this)
        }
        Logger.warn(this, "unable to access dialog without activity")
        return null
    }
    
    // ----------------------------------------
    // Events
    
    /** The dialog will become visible. */
    fun onPrepare() {
        // implement to prepare resources
    }
    
    /** The dialog is now hidden. */
    fun onRelease() {
        // implement to release resources
    }
    
    // ----------------------------------------
    // Show / Hide / Update
    
    fun show(onEvent: DialogEventReceiver) {
        show(onEvent=onEvent, onAction=null)
    }
    
    fun show(onEvent: DialogEventReceiver?, onAction: DialogActionReceiver?) {
        show(listener = object: DialogListener {
            override fun onEvent(dialog: DashDialog, event: DialogEvent) {
                if (onEvent != null) {
                    onEvent(dialog, event)
                }
            }
            override fun onAction(dialog: DashDialog, action: DialogAction) {
                if (onAction != null) {
                    onAction(dialog, action)
                }
            }
        })
    }
    
    fun show(listener: DialogListener? = null): DashDialog? {
        val activity = Dash.currentActivity as? DashActivity
        if (activity != null) {
            return activity.showDialog(this, listener)
        }
        Logger.warn(this, "unable to show dialog without activity")
        return null
    }
    
    fun hide() {
        this.dialog?.hide()
    }
}