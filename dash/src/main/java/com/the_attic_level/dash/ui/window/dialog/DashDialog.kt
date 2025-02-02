package com.the_attic_level.dash.ui.window.dialog

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.layout.type.UIText
import com.the_attic_level.dash.ui.window.DashWindow

typealias DialogClass = Class<out DashDialog>
typealias DialogStyle = UIStyle.Dialog

open class DashDialog(activity: Activity, override val style: DialogStyle): DashWindow(activity, style)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        var defaultDialogClass   : DialogClass = DashDialog    ::class.java
        var defaultProgressClass : DialogClass = ProgressDialog::class.java
        
        fun create(activity: Activity, cls: DialogClass, style: Style): DashDialog? {
            return try {
                val init = cls.declaredConstructors[0]
                if (init.parameterTypes.size == 2) {
                    cls.cast(init.newInstance(activity, style))
                } else {
                    cls.cast(init.newInstance(activity))
                }
            } catch (e: Exception) {
                Logger.error(DashDialog::class, "unable to create dialog: ${e.message} ${e.cause?.message}")
                null
            }
        }
    }
    
    // ----------------------------------------
    // Interface
    
    interface Style: DashWindow.Style
    {
        val progressWheelSize : Int
        val minButtonWidth    : Int
        
        val title  : UIText
        val text   : UIText
        val button : UIText
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private var close = false
    
    // ----------------------------------------
    // Members
    
    var context  : DialogContext?  = null; private set
    var listener : DialogListener? = null; private set
    var action   : DialogAction?   = null; private set
    
    // ----------------------------------------
    // Members (Protected)
    
    protected var titleView    : TextView?  = null
    protected var textView     : TextView?  = null
    protected var buttonLayout : ViewGroup? = null
    
    // ----------------------------------------
    // Methods
    
    fun show(context: DialogContext, listener: DialogListener?) {
        this.context  = context
        this.listener = listener
        show()
    }
    
    /** Keeps the dialog open after an dialog action was invoked. */
    fun keepOpen() {
        this.close = false
    }
    
    /** Enables/Disables a dialog button with the given action. */
    fun enableButton(action: DialogAction, enabled: Boolean) {
        val buttons = this.buttonLayout ?: return
        for (i in 0 until buttons.childCount) {
            val child = buttons.getChildAt(i)
            if (child.tag === action) {
                child.isEnabled = enabled
                return
            }
        }
    }
    
    // ----------------------------------------
    // Methods
    
    protected open fun onSetup()
    {
        val context = this.context ?: return
        
        val title = context.title
        val text  = context.text
        
        if (title.isNullOrEmpty()) {
            this.titleView?.visibility = View.GONE
        } else {
            this.titleView?.visibility = View.VISIBLE
            this.titleView?.text = title
        }
        
        if (text.isNullOrEmpty()) {
            this.textView?.visibility = View.GONE
        } else {
            this.textView?.visibility = View.VISIBLE
            this.textView?.text = text
        }
        
        updateButtons(context.actions)
        updateContentPadding()
    }
    
    protected open val contentPaddingTop: Int
        get() = if (UI.visible(this.titleView)) 0 else UI.scale(64)
    
    protected open val contentPaddingBottom: Int
        get() = if (UI.visible(this.buttonLayout)) 0 else UI.scale(64)
    
    protected open fun updateContentPadding()
    {
        val contentView = this.contentView ?: return
        
        val top    = this.contentPaddingTop
        val bottom = this.contentPaddingBottom
        
        if (contentView.paddingTop != top || contentView.paddingBottom != bottom) {
            contentView.updatePadding(top=top, bottom=bottom)
        }
    }
    
    // ----------------------------------------
    // Dash Panel
    
    override fun onCreate(): View
    {
        val vertical = UI.vertical(this.activity, UI.MATCH_WRAP)
        
        onCreateHeader (vertical)
        onCreateContent(vertical)
        onCreateButtons(vertical)
        
        return vertical
    }
    
    override fun onStateChanged() {
        when (this.state) {
            State.SHOW -> {
                this.action = null
                this.context?.onPrepare()
                this.listener?.onEvent(this, DialogEvent.ON_SHOW)
                onSetup()
            }
            State.VISIBLE -> {
                this.listener?.onEvent(this, DialogEvent.ON_VISIBLE)
            }
            State.HIDE -> {
                this.listener?.onEvent(this, DialogEvent.ON_HIDE)
            }
            State.HIDDEN -> {
                this.listener?.onEvent(this, DialogEvent.ON_HIDDEN)
                this.context?.onRelease()
                this.listener = null
                this.context  = null
            }
        }
    }
    
    // ----------------------------------------
    // Create Content
    
    protected open fun onCreateHeader(content: LinearLayout) {
        this.titleView = createTitleView()
        content.addView(this.titleView)
    }
    
    protected open fun onCreateContent(content: LinearLayout) {
        this.textView = createTextView()
        content.addView(this.textView)
    }
    
    protected open fun onCreateButtons(content: LinearLayout) {
        this.buttonLayout = createButtonLayout()
        content.addView(this.buttonLayout)
    }
    
    /** Default handler for creating a new dialog button. */
    protected open fun createButton(): View = UI.text(this.activity, UI.WRAP_MATCH, this.style.button).also {
        it.minimumWidth = this.style.minButtonWidth
        it.setOnClickListener(this::onButtonClick)
    }
    
    /** Default handler for creating the title view. */
    protected open fun createTitleView() = UI.text(this.activity, UI.WRAP, this.style.title).also {
        it.visibility = View.GONE
    }
    
    /** Default handler for creating the message view. */
    protected open fun createTextView() = UI.text(this.activity, UI.MATCH_WRAP, this.style.text).also {
        it.visibility = View.GONE
    }
    
    /** Default handler for creating the button layout. */
    protected open fun createButtonLayout() = UI.horizontal(this.activity, UI.MATCH_WRAP).also {
        it.setPadding(UI.scale(20), 0, UI.scale(20), 0)
        it.gravity = Gravity.END
    }
    
    // ----------------------------------------
    // Button Handling
    
    /** Default handler for presenting dialog actions in the button layout. */
    protected open fun updateButtons(actions: Array<DialogAction>)
    {
        val buttons = this.buttonLayout ?: return
        
        // hide all views inside the button layout
        UI.childVisibility(buttons, View.GONE)
        
        if (actions.isNotEmpty()) {
            buttons.visibility = View.VISIBLE
            for (action in actions) {
                var child = UI.findChildByVisibility(buttons, View::class.java, View.GONE)
                if (child == null) {
                    child = createButton()
                    buttons.addView(child)
                }
                setupButton(child, action)
            }
        } else {
            // hides the entire button layout
            buttons.visibility = View.GONE
        }
    }
    
    /** Default handler for applying a response action to the given dialog button. */
    protected open fun setupButton(view: View, action: DialogAction)
    {
        if (view.tag !== action) {
            if (view is TextView) {
                view.text = action.name
            }
            view.tag = action
        }
        
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }
    
    /** Default click listener for the response buttons. */
    protected open fun onButtonClick(view: View) {
        if (view.tag is DialogAction) {
            onButtonAction((view.tag as DialogAction))
        }
    }
    
    /** Default handler for dialog actions. */
    protected open fun onButtonAction(action: DialogAction)
    {
        this.action = action
        this.close  = true
        
        if (action.isPositive) {
            this.listener?.onEvent(this, DialogEvent.ON_POSITIVE)
        } else if (action.isNegative) {
            this.listener?.onEvent(this, DialogEvent.ON_NEGATIVE)
        } else {
            this.listener?.onEvent(this, DialogEvent.ON_NEUTRAL)
        }
        
        this.listener?.onAction(this, action)
        
        if (this.close) {
            hide()
        }
    }
}