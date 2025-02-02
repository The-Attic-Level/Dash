package com.the_attic_level.dash.ui.fragment

import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.layout.type.UIRect
import com.the_attic_level.dash.ui.widget.DashParent

open class DashFragmentGroup(
    parent: DashParent, id: ID,
    val ids  : Array<out ID>,
    val type : Type,
    val rect : UIRect
): DashFragment(parent, id)
{
    // ----------------------------------------
    // Static
    
    companion object {
        val FRAGMENT_GROUP_ID = createID(DashFragmentGroup::class.java)
    }
    
    // ----------------------------------------
    // Members (Final)
    
    protected val fragments: List<DashFragment> by lazy()
    {
        val list = ArrayList<DashFragment>(this.ids.size)
        
        for (fid in this.ids) {
            val fragment = fid.instantiate(this)
            if (fragment != null) {
                list.add(fragment)
            }
        }
        
        return@lazy list
    }
    
    // ----------------------------------------
    // Members
    
    var active: DashFragment? = null
        private set
    
    // ----------------------------------------
    // Properties
    
    val initial: ID?
        get() = if (this.fragments.isNotEmpty()) this.fragments[0].id else null
    
    // ----------------------------------------
    // Init
    
    constructor(parent: DashParent, ids: Array<out ID>, type: Type, rect: UIRect):
            this(parent, FRAGMENT_GROUP_ID, ids, type, rect)
    
    // ----------------------------------------
    // Methods
    
    open fun show(id: ID?) {
        val fragment = find(id)
        if (fragment != null) {
            if (this.isActive) {
                fragment.show()
            } else {
                attach()
                fragment.attach()
            }
        }
    }
    
    open fun hide(id: ID?) {
        find(id)?.hide()
    }
    
    open fun find(id: ID?): DashFragment? {
        for (fragment in this.fragments) {
            if (fragment.id === id) {
                return fragment
            }
        }
        return null
    }
    
    open fun <T: DashFragment?> find(cls: Class<T>): T? {
        for (fragment in this.fragments) {
            if (cls.isInstance(fragment)) {
                return cls.cast(fragment)
            }
        }
        return null
    }
    
    // ----------------------------------------
    // Dash Layer
    
    override fun interceptBackPress(): Boolean {
        return this.active?.interceptBackPress() ?: false
    }
    
    override fun onCreate() {
        setup(this.type, this.rect)
    }
    
    override fun onUpdate(param: Any?) {
        this.active?.update(param)
    }
    
    // ----------------------------------------
    // Dash Parent
    
    override fun onEvent(child: DashFragment, event: Event)
    {
        // pass event further to parent
        super.onEvent(child, event)
        
        // check if the given fragment is
        // a direct child of this group
        if (child.parent !== this) {
            return
        }
        
        if (event == Event.ON_SHOW) {
            this.active = child
            for (other in fragments) {
                if (other !== child) {
                    other.hide(child.isAnimated)
                }
            }
        } else if (event == Event.ON_HIDE) {
            if (this.active === child) {
                this.active = null
            }
        }
    }
    
    // ----------------------------------------
    // Dash Fragment
    
    override fun onEvent(activity: DashActivity, event: ActivityEvent)
    {
        super.onEvent(activity, event)
        
        // pass activity event through all child fragments
        for (fragment in this.fragments) {
            fragment.onEvent(activity, event)
        }
    }
}