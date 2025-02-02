package com.the_attic_level.dash.sys.file

import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.sys.cipher.CipherKey
import com.the_attic_level.dash.sys.properties.Properties
import org.json.JSONObject

open class JSONFile(val file: DashFile): Properties.Source
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        /** Creates a handle for a json-file in the local storage folder. */
        fun internal(name: String, key: CipherKey? = null): JSONFile {
            return JSONFile(DashFile.internal(name, key))
        }
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private lateinit var data: JSONObject
    private var loaded = false
    
    // ----------------------------------------
    // Properties Source
    
    override val name: String
        get() = this.file.name
    
    override val json: JSONObject; get() {
        if (!this.loaded) {
            synchronized(this) {
                if (!this.loaded) {
                    this.data  = Dash.toJSON(this.file.readBytes()) ?: JSONObject()
                    this.loaded = true
                }
            }
        }
        return this.data
    }
    
    override fun save() {
        if (this.loaded) {
            synchronized(this) {
                this.file.writeBytes(Dash.toBytes(this.data))
            }
        }
    }
    
    override fun clear() {
        if (this.loaded) {
            synchronized(this) {
                this.data = JSONObject()
                this.file.writeBytes(Dash.toBytes(this.data))
            }
        }
    }
}