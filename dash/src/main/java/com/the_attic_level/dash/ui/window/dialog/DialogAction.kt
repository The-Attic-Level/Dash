package com.the_attic_level.dash.ui.window.dialog

import com.the_attic_level.dash.R
import com.the_attic_level.dash.app.Dash

class DialogAction(val name: String, val feedback: Byte)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        const val FEEDBACK_NEGATIVE : Byte = -1
        const val FEEDBACK_NEUTRAL  : Byte = 0
        const val FEEDBACK_POSITIVE : Byte = 1
        
        val YES      = DialogAction(R.string.button_yes,      FEEDBACK_POSITIVE)
        val OK       = DialogAction(R.string.button_ok,       FEEDBACK_NEUTRAL)
        val NO       = DialogAction(R.string.button_no,       FEEDBACK_NEGATIVE)
        val CANCEL   = DialogAction(R.string.button_cancel,   FEEDBACK_NEGATIVE)
        val SAVE     = DialogAction(R.string.button_save,     FEEDBACK_POSITIVE)
        val SEND     = DialogAction(R.string.button_send,     FEEDBACK_POSITIVE)
        val SHARE    = DialogAction(R.string.button_share,    FEEDBACK_POSITIVE)
        val APPLY    = DialogAction(R.string.button_apply,    FEEDBACK_POSITIVE)
        val CONTINUE = DialogAction(R.string.button_continue, FEEDBACK_POSITIVE)
    }
    
    // ----------------------------------------
    // Init
    
    constructor(res: Int, feedback: Byte): this(Dash.string(res), feedback)
    
    // ----------------------------------------
    // Properties
    
    val isPositive: Boolean
        get() = this.feedback == FEEDBACK_POSITIVE
    
    val isNeutral: Boolean
        get() = this.feedback == FEEDBACK_NEUTRAL
    
    val isNegative: Boolean
        get() = this.feedback == FEEDBACK_NEGATIVE
}