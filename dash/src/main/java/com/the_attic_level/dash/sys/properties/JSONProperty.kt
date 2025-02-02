package com.the_attic_level.dash.sys.properties

import org.json.JSONObject

class JSONProperty(
    private val properties : Properties,
    private val id         : Properties.ID
): Properties.Source {
    // ----------------------------------------
    // Members
    
    private var data: JSONObject? = null
    
    // ----------------------------------------
    // Properties
    
    override var json: JSONObject
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optJSONObject(this.name)
                    if (this.data == null) {
                        this.data = JSONObject()
                        it.put(this.name, this.data)
                    }
                }
            }
            return this.data!!
        }
        set(value)
        {
            this.properties.save {
                it.put(this.name, value)
                this.data = value
                true
            }
        }
    
    // ----------------------------------------
    // Methods
    
    override val name: String
        get() = this.id.name
    
    override fun save() {
        if (this.data != null) {
            this.properties.save()
        }
    }
    
    override fun clear() {
        this.properties.save {
            it.remove(this.name)
            this.data = null
            true
        }
    }
}