package com.the_attic_level.dash.ui.layout

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.the_attic_level.dash.sys.Logger
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class UIMetrics
{
    // ----------------------------------------
    // Singleton
    
    companion object
    {
        /** Multiplier to convert from inch to centimeters. */
        const val INCH_TO_CM = 2.54F
        
        val shared = UIMetrics()
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private val display = Point()
    private var initialized = false
    
    // ----------------------------------------
    // Members
    
    // minimum and maximum display size
    var minDisplaySize = 0; private set
    var maxDisplaySize = 0; private set
    
    // minimum and maximum physical display size in cm
    var minDisplayCM = 0.0F; private set
    var maxDisplayCM = 0.0F; private set
    
    /** Display-Ratio (max/min) */
    var displayRatio = 0.0F; private set
    
    /** Physical display diagonal in cm. */
    var diagonalCM = 0.0F; private set
    
    // ----------------------------------------
    // Properties
    
    val portrait;  get() = this.display.x < this.display.y
    val landscape; get() = this.display.x > this.display.y
    
    val displayW; get() = this.display.x
    val displayH; get() = this.display.y
    
    // ----------------------------------------
    // Setup
    
    fun setup(context: Context): Boolean
    {
        // get window manager or return without changes
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager? ?: return false
        
        // get previous display size
        val x = this.display.x
        val y = this.display.y
        
        // get current display size (entire device)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val rect = manager.currentWindowMetrics.bounds
            this.display.x = rect.width()
            this.display.y = rect.height()
        } else {
            @Suppress("DEPRECATION")
            manager.defaultDisplay.getRealSize(this.display)
        }
        
        // check if the display size has changed
        if (x != this.display.x || y != this.display.y)
        {
            if (!this.initialized)
            {
                // get minimum and maximum display size
                this.minDisplaySize = min(this.display.x, this.display.y)
                this.maxDisplaySize = max(this.display.x, this.display.y)
                
                // calculate display ratio
                this.displayRatio = this.maxDisplaySize.toFloat() / this.minDisplaySize.toFloat()
                
                // calculate physical display size
                val metrics = context.resources.displayMetrics
                
                if (metrics.xdpi > 0.0F && metrics.ydpi > 0.0F) {
                    this.minDisplayCM = this.minDisplaySize.toFloat() / metrics.xdpi * INCH_TO_CM
                    this.maxDisplayCM = this.maxDisplaySize.toFloat() / metrics.ydpi * INCH_TO_CM
                    this.diagonalCM = UIMath.length(this.minDisplayCM, this.maxDisplayCM)
                }
                
                this.initialized = true
            }
            
            // orientation or size changed
            return true
        }
        
        // no changes
        return false
    }
    
    // ----------------------------------------
    // Print
    
    fun print()
    {
        val minCM    = String.format(Locale.ENGLISH, "%.1f", this.minDisplayCM)
        val maxCM    = String.format(Locale.ENGLISH, "%.1f", this.maxDisplayCM)
        val diagonal = String.format(Locale.ENGLISH, "%.2f", this.diagonalCM)
        val ratio    = String.format(Locale.ENGLISH, "%.3f", this.displayRatio)
        
        Logger.info(this, "current_display [${this.display.x} x ${this.display.y} pixels] " +
                "fixed_display [${this.minDisplaySize} x ${this.maxDisplaySize} pixels] " +
                "[$minCM x $maxCM cm] diagonal [$diagonal cm] ratio (max/min) [$ratio]"
        )
    }
}