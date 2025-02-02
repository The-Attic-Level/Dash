package com.the_attic_level.dash.sys.properties

class StringProperty(
    private val properties : Properties,
    private val id         : Properties.ID,
    private val fallback   : String = ""
){
    // ----------------------------------------
    // Members
    
    private var data: String? = null
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.id.name
    
    var value: String
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optString(this.name, this.fallback)
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
}