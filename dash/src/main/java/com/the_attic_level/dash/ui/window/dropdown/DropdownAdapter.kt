package com.the_attic_level.dash.ui.window.dropdown

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

open class DropdownAdapter(val context: Context, val style: DropdownStyle): BaseAdapter(), DashDropdown.Adapter
{
    // ----------------------------------------
    // Members (Final)
    
    protected val entries = ArrayList<DashDropdown.Entry>(32)
    
    // ----------------------------------------
    // Dropdown Adapter
    
    override fun reset() {
        this.entries.clear()
        notifyDataSetChanged()
    }
    
    override fun setup(entries: List<DashDropdown.Entry>) {
        this.entries.clear()
        this.entries.addAll(entries)
        notifyDataSetChanged()
    }
    
    // ----------------------------------------
    // Base Adapter
    
    override fun getCount(): Int {
        return this.entries.size
    }
    
    override fun getItem(position: Int): Any {
        return this.entries[position]
    }
    
    override fun getItemId(position: Int): Long {
        return 0
    }
    
    override fun getView(position: Int, convert: View?, parent: ViewGroup?): View
    {
        val view = if (convert != null) {
            convert as DropdownView
        } else {
            DropdownView.create(this.context, this.style)
        }
        
        view.setup(getItem(position) as DashDropdown.Entry)
        return view
    }
}