package com.the_attic_level.dash.sys.properties

class DoubleProperty(
    private val properties : Properties,
    private val id         : Properties.ID,
    private val fallback   : Double = 0.0
){
    // ----------------------------------------
    // Members
    
    private var data: Double? = null
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.id.name
    
    var value: Double
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optDouble(this.name, this.fallback)
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