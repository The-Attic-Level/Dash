package com.the_attic_level.dash.sys.properties

class BoolProperty(
    private val properties : Properties,
    private val id         : Properties.ID,
    private val fallback   : Boolean = false
){
    // ----------------------------------------
    // Members
    
    private var data: Boolean? = null
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.id.name
    
    var value: Boolean
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optBoolean(this.name, this.fallback)
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
    
    fun toggle() {
        this.value = !this.value
    }
}