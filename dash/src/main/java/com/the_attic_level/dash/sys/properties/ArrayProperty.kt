package com.the_attic_level.dash.sys.properties

import org.json.JSONArray

class ArrayProperty(
    private val properties : Properties,
    private val id         : Properties.ID
){
    // ----------------------------------------
    // Members
    
    private var data: JSONArray? = null
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.id.name
    
    var value: JSONArray
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optJSONArray(this.name)
                    if (this.data == null) {
                        this.data = JSONArray()
                        it.put(this.name, this.data)
                    }
                }
            }
            return this.data!!
        }
        set(value) {
            this.properties.save {
                it.put(this.name, value)
                this.data = value
                true
            }
        }
    
    // ----------------------------------------
    // Methods
    
    fun save() {
        this.properties.save()
    }
}