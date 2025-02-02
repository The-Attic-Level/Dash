package com.the_attic_level.dash.ui.window.dropdown

import android.app.Activity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.UI
import com.the_attic_level.dash.ui.UIStyle
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.UIText
import com.the_attic_level.dash.ui.painter.icon.Icon
import com.the_attic_level.dash.ui.widget.drawable.ColorDrawable
import com.the_attic_level.dash.ui.window.DashWindow

typealias Dropdown      = DashDropdown.Builder
typealias DropdownClass = Class<out DashDropdown>
typealias DropdownStyle = DashDropdown.Style

open class DashDropdown(
    activity: Activity,
    override val style: DropdownStyle = UIStyle.shared.dropdown
): DashWindow(activity, style), OnItemClickListener
{
    // ----------------------------------------
    // Interface
    
    interface Entry
    {
        val dropdownValue: CharSequence
        
        val dropdownIcon: Icon?
            get() = null
        
        val dropdownIconScale: Float
            get() = 1.0F
    }
    
    interface Adapter: ListAdapter {
        fun reset()
        fun setup(entries: List<Entry>)
    }
    
    interface Listener {
        fun onEntrySelected(entry: Entry)
    }
    
    interface Style: DashWindow.Style
    {
        var dividerColor   : UIR
        var dividerHeight  : Int
        val minEntryHeight : Int
        
        var iconColor      : UIR
        var iconSize       : Int
        var iconSpace      : Int
        
        val popupTitle     : UIText
        val entryTitle     : UIText
        
        fun setupDivider(view: ListView) {
            view.divider = ColorDrawable(this.dividerColor)
            view.dividerHeight = this.dividerHeight
        }
    }
    
    // ----------------------------------------
    // Class
    
    open class Builder(
        val cls     : DropdownClass = defaultClass,
        val style   : DropdownStyle = UIStyle.shared.dropdown,
        val entries : List<Entry>)
    {
        var title: CharSequence? = null
        
        fun show(listener: (Entry) -> Unit) {
            (Dash.currentActivity as? DashActivity)?.showDropdown(this, object : Listener {
                override fun onEntrySelected(entry: Entry) {
                    listener(entry)
                }
            })
        }
        
        fun show(listener: Listener) {
            (Dash.currentActivity as? DashActivity)?.showDropdown(this, listener)
        }
    }
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        var defaultClass: DropdownClass = DashDropdown::class.java
        
        fun create(cls: DropdownClass, style: DropdownStyle, activity: DashActivity): DashDropdown? {
            return try {
                cls.cast(cls.declaredConstructors[0].newInstance(activity, style))
            } catch (e: Exception) {
                Logger.error(DashDropdown::class, "unable to create dropdown: ${e.message}")
                null
            }
        }
    }
    
    // ----------------------------------------
    // Members
    
    protected var adapter   : Adapter?  = null
    protected var titleView : TextView? = null
    protected var listView  : ListView? = null
    protected var listener  : Listener? = null
    protected var builder   : Builder?  = null
    
    // ----------------------------------------
    // Methods
    
    fun show(builder: Builder, listener: Listener)
    {
        this.builder  = builder
        this.listener = listener
        
        show()
    }
    
    // ----------------------------------------
    // On Item Click Listener
    
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
    {
        hide()
        
        val adapter = this.adapter
        
        if (adapter != null) {
            this.listener?.onEntrySelected(adapter.getItem(position) as Entry)
        }
    }
    
    // ----------------------------------------
    // Dash Window
    
    override fun onCreate(): View
    {
        // create content view
        val vertical = UI.vertical(this.activity, UI.MATCH_WRAP)
        
        onCreateHeader  (vertical)
        onCreateListView(vertical)
        
        return vertical
    }
    
    override fun onStateChanged() {
        if (this.state == State.SHOW) {
            // reset previous list selection
            this.listView?.setSelection(-1)
            val builder = this.builder
            if (builder != null) {
                onSetup(builder)
            }
        } else if (this.state == State.HIDDEN) {
            this.listener = null
            this.builder  = null
        }
    }
    
    // ----------------------------------------
    // Methods (Protected)
    
    /** Override to use your own adapter. */
    protected open fun onCreateAdapter(): Adapter {
        return DropdownAdapter(this.activity, this.style)
    }
    
    /** Override to insert additional views. */
    protected open fun onCreateHeader(vertical: LinearLayout) {
        this.titleView = UI.text(this.activity, UI.MATCH_WRAP, this.style.popupTitle)
        vertical.addView(this.titleView)
    }
    
    /** Override to insert additional views. */
    protected open fun onCreateListView(vertical: LinearLayout)
    {
        this.adapter = onCreateAdapter()
        
        this.listView = UI.list(this.activity, UI.MATCH_WRAP).also {
            this.style.setupDivider(it)
            it.isScrollbarFadingEnabled = false
            it.onItemClickListener = this
            it.adapter = this.adapter
        }
        
        vertical.addView(this.listView)
    }
    
    /** Override for additional setup. */
    protected open fun onSetup(builder: Builder)
    {
        this.adapter?.setup(builder.entries)
        
        if (builder.title.isNullOrEmpty()) {
            UI.visibility(this.titleView, View.GONE)
        } else {
            UI.visibility(this.titleView, View.VISIBLE)
            this.titleView?.text = builder.title
        }
    }
}