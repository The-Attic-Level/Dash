package com.the_attic_level.dash.sys.properties

import com.the_attic_level.dash.sys.cipher.CipherKey
import com.the_attic_level.dash.sys.file.JSONFile
import com.the_attic_level.dash.sys.Logger
import org.json.JSONArray
import org.json.JSONObject

open class Properties(protected val source: Source)
{
    // ----------------------------------------
    // Interface
    
    interface ID {
        val name: String
    }
    
    interface Source
    {
        val name: String
        val json: JSONObject
        
        fun save()
        fun clear()
    }
    
    // ----------------------------------------
    // Class
    
    class Key(override val name: String): ID
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        /** Create properties using a json-file on the local storage. */
        fun internal(name: String, key: CipherKey? = null): Properties {
            return Properties(JSONFile.internal(name, key))
        }
    }
    
    // ----------------------------------------
    // Properties
    
    val name: String
        get() = this.source.name
    
    // ----------------------------------------
    // Methods
    
    fun save() {
        this.source.save()
    }
    
    fun clear() {
        this.source.clear()
    }
    
    fun contains(id: ID): Boolean {
        synchronized(this.source) {
            return this.source.json.has(id.name)
        }
    }
    
    fun remove(id: ID): Any? {
        synchronized(this.source) {
            return this.source.json.remove(id.name)
        }
    }
    
    fun use(action: (JSONObject) -> Unit) {
        synchronized(this.source) {
            try {
                action(this.source.json)
            } catch (e: Exception) {
                Logger.error(this, "error while using properties '${this.source.name}': ${e.message}")
            }
        }
    }
    
    fun save(action: (JSONObject) -> Boolean) {
        synchronized(this.source) {
            try {
                if (action(this.source.json)) {
                    this.source.save()
                }
            } catch (e: Exception) {
                Logger.error(this, "error while modifying properties '${this.source.name}': ${e.message}")
            }
        }
    }
    
    // ----------------------------------------
    // Getter
    
    fun getString(id: ID, fallback: String = ""): String {
        synchronized(this.source) {
            return this.source.json.optString(id.name, fallback)
        }
    }
    
    fun getBoolean(id: ID, fallback: Boolean = false): Boolean {
        synchronized(this.source) {
            return this.source.json.optBoolean(id.name, fallback)
        }
    }
    
    fun getInt(id: ID, fallback: Int = 0): Int {
        synchronized(this.source) {
            return this.source.json.optInt(id.name, fallback)
        }
    }
    
    fun getLong(id: ID, fallback: Long = 0L): Long {
        synchronized(this.source) {
            return this.source.json.optLong(id.name, fallback)
        }
    }
    
    fun getDouble(id: ID, fallback: Double = 0.0): Double {
        synchronized(this.source) {
            return this.source.json.optDouble(id.name, fallback)
        }
    }
    
    fun getJSON(id: ID): JSONObject? {
        synchronized(this.source) {
            return this.source.json.optJSONObject(id.name) ?: null
        }
    }
    
    fun getArray(id: ID): JSONArray? {
        synchronized(this.source) {
            return this.source.json.optJSONArray(id.name) ?: null
        }
    }
    
    // ----------------------------------------
    // Setter
    
    fun set(id: ID, value: String)     = use { it.put(id.name, value) }
    fun set(id: ID, value: Boolean)    = use { it.put(id.name, value) }
    fun set(id: ID, value: Int)        = use { it.put(id.name, value) }
    fun set(id: ID, value: Long)       = use { it.put(id.name, value) }
    fun set(id: ID, value: Double)     = use { it.put(id.name, value) }
    fun set(id: ID, value: JSONObject) = use { it.put(id.name, value) }
    fun set(id: ID, value: JSONArray)  = use { it.put(id.name, value) }
}