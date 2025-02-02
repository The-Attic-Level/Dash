package com.the_attic_level.dash.sys.properties

class LongProperty(
    private val properties : Properties,
    private val id         : Properties.ID,
    private val fallback   : Long = 0L
){
    // ----------------------------------------
    // Members
    
    private var data: Long? = null
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.id.name
    
    var value: Long
        get() {
            if (this.data == null) {
                this.properties.use {
                    this.data = it.optLong(this.name, this.fallback)
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