package com.the_attic_level.dash.ui.fragment

import com.the_attic_level.dash.app.DashActivity
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.activity.ActivityState
import com.the_attic_level.dash.ui.widget.DashLayer
import com.the_attic_level.dash.ui.widget.DashParent
import java.lang.reflect.Constructor

typealias FragmentClass = Class<out DashFragment>
typealias FragmentEvent = DashFragment.Event

open class DashFragment(parent: DashParent, val id: ID): DashLayer(parent)
{
    // ----------------------------------------
    // Enum
    
    enum class Event
    {
        ON_SHOW,
        ON_VISIBLE,
        ON_HIDE,
        ON_HIDDEN,
        
        /** The activity has been resumed and the fragment will become visible or is already visible. */
        ON_RESUMED,
        
        /** Either the activity has been paused or the fragment will become hidden. */
        ON_PAUSED
    }
    
    // ----------------------------------------
    // Interface
    
    interface ID
    {
        /** Class to instantiate the fragment.  */
        val cls: FragmentClass
        
        fun instantiate(parent: DashParent): DashFragment? {
            return try {
                val init: Constructor<*> = this.cls.declaredConstructors[0]
                if (init.parameterTypes.size == 2) {
                    init.newInstance(parent, this) as DashFragment
                } else {
                    init.newInstance(parent) as DashFragment
                }
            } catch (e: Exception) {
                Logger.error(this.cls, "unable to instantiate fragment [${this.cls.simpleName}]: ${e.message}")
                null
            }
        }
    }
    
    @Deprecated("")
    interface Parent {
        /** Notification about an activity state change relative to the child visibility. */
        fun onEvent(child: DashFragment, event: Event)
    }
    
    // ----------------------------------------
    // Static
    
    companion object {
        fun createID(cls: FragmentClass): ID {
            return object: ID { override val cls = cls }
        }
    }
    
    // ----------------------------------------
    // Members
    
    private var resumed = false
    
    // ----------------------------------------
    // Methods
    
    open fun onEvent(activity: DashActivity, event: ActivityEvent) {
        // override to handle activity events
        if (this.isActive) {
            if (event == ActivityEvent.ON_RESUMED) {
                if (!this.resumed) {
                    this.resumed = true
                    onEvent(Event.ON_RESUMED)
                }
            } else if (event == ActivityEvent.ON_PAUSED) {
                if (this.resumed) {
                    this.resumed = false
                    onEvent(Event.ON_PAUSED)
                }
            }
        }
    }
    
    // ----------------------------------------
    // View Layer
    
    /** Converts layer state changes into fragment events. */
    override fun onStateChanged() {
        if (this.state == State.SHOW) {
            onEvent(Event.ON_SHOW)
            if (!this.resumed && this.activity.state == ActivityState.RESUMED) {
                this.resumed = true
                onEvent(Event.ON_RESUMED)
            }
        } else if (this.state == State.VISIBLE) {
            onEvent(Event.ON_VISIBLE)
        } else if (this.state == State.HIDE) {
            onEvent(Event.ON_HIDE)
            if (this.resumed) {
                this.resumed = false
                onEvent(Event.ON_PAUSED)
            }
        } else if (this.state == State.HIDDEN) {
            onEvent(Event.ON_HIDDEN)
        }
    }
    
    // ----------------------------------------
    // Fragment Events
    
    protected open fun onEvent(event: Event) {
        // pass event further to parent
        this.parent.onEvent(this, event)
    }
}