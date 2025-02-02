package com.the_attic_level.dash.ui.fragment

import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.layout.type.UIRect
import com.the_attic_level.dash.ui.widget.DashLayer

open class DashFragmentActivity(ids: Array<out DashFragment.ID> = emptyArray()): DashActivity()
{
    // ----------------------------------------
    // Members
    
    protected val group: DashFragmentGroup? by lazy {
        if (ids.isNotEmpty()) {
            return@lazy onCreateFragmentGroup(ids)
        }
        return@lazy null
    }
    
    // ----------------------------------------
    // Dash Activity
    
    override fun interceptBackPress(): Boolean {
        return super.interceptBackPress() || (this.group?.interceptBackPress() ?: false)
    }
    
    // ----------------------------------------
    // Fragment Handling
    
    val activeFragment: DashFragment?
        get() = this.group?.active
    
    open fun show(id: DashFragment.ID) {
        this.group?.show(id)
    }
    
    open fun hide(id: DashFragment.ID) {
        this.group?.hide(id)
    }
    
    open fun find(id: DashFragment.ID): DashFragment? {
        return this.group?.find(id)
    }
    
    open fun <T: DashFragment> find(cls: Class<T>): T? {
        return this.group?.find(cls)
    }
    
    // ----------------------------------------
    // Dash Activity
    
    override fun onCreate()
    {
        val group = this.group ?: return
        
        // create group layout and add it to activity content view
        group.attach()
        
        if (group.active == null) {
            group.show(this.initialFragment)
        }
    }
    
    // ----------------------------------------
    // Activity Lifecycle
    
    override fun onEvent(event: ActivityEvent) {
        super.onEvent(event)
        this.group?.onEvent(this, event)
    }
    
    // ----------------------------------------
    // Methods
    
    /** Returns the id of the fragment that should be displayed after the activity has been created. */
    protected open val initialFragment: DashFragment.ID?; get() {
        return this.group?.initial
    }
    
    /** Override to create your custom fragment group. */
    protected open fun onCreateFragmentGroup(ids: Array<out DashFragment.ID>): DashFragmentGroup {
        return DashFragmentGroup(this, ids = ids, type = DashLayer.Type.FRAME, rect = UIRect.MATCH)
    }
}