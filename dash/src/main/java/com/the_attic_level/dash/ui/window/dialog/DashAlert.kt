package com.the_attic_level.dash.ui.window.dialog

import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.ternary
import com.the_attic_level.dash.ui.UIStyle

open class DashAlert(
    override val cls     : DialogClass = DashDialog.defaultDialogClass,
    override val style   : DialogStyle = UIStyle.shared.dialog,
    override val actions : Array<DialogAction>
): DialogContext
{
    // ----------------------------------------
    // Members (Final)
    
    override var title : CharSequence? = null
    override var text  : CharSequence? = null
    override var tag   : Any?          = null
    
    override var allowsImplicitCancellation = true
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        val OK_ACTION       = arrayOf(DialogAction.OK)
        val REQUEST_ACTIONS = arrayOf(DialogAction.NO, DialogAction.YES)
        val CANCEL_ACTION   = arrayOf(DialogAction.CANCEL)
        val NO_ACTIONS      = arrayOf<DialogAction>()
        
        /** Dialog for displaying information with an 'OK' button. */
        fun info() = DashAlert(actions = OK_ACTION)
        
        /** A request dialog with the options 'NO' and 'YES'. */
        fun request() = DashAlert(actions = REQUEST_ACTIONS)
        
        /**
         * Dialog for indicating an ongoing progress.
         * A sticky progress dialog can't be cancelled by the user.
         * An example would be a logout process.
         */
        fun progress(sticky: Boolean = false) = DashAlert(
            cls = DashDialog.defaultProgressClass,
            actions = ternary(sticky, NO_ACTIONS, CANCEL_ACTION)
        ).disableImplicitCancellation()
    }
    
    // ----------------------------------------
    // Methods
    
    fun setup(title: Int, text: Int, vararg args: Any?) = also {
        this.title = Dash.optString(title)
        this.text  = Dash.optString(text, args)
    }
    
    fun setup(title: CharSequence?, text: CharSequence?) = also {
        this.title = title
        this.text  = text
    }
    
    fun disableImplicitCancellation(): DashAlert {
        this.allowsImplicitCancellation = false
        return this
    }
}