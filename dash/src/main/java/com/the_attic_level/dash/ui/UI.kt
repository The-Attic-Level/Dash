package com.the_attic_level.dash.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.graphics.Insets
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.ui.activity.InsetRequest
import com.the_attic_level.dash.ui.layout.UIMethod
import com.the_attic_level.dash.ui.layout.UIMetrics
import com.the_attic_level.dash.ui.layout.type.UIRect
import kotlin.math.max

class UI
{
    companion object: UIHelper
    {
        // ----------------------------------------
        // Constants (Scaling)
        
        /**
         *  With the reference size you can use fixed pixel sizes to scale your layout to any display resolution.
         *  The idea is to use fixed pixel sizes to define your layout, and those fixed sizes get scaled linearly
         *  to the display resolution of the respective device. Alternatively you can use simple scales (0.0 to 1.0)
         *  to define your layout, but in most cases it's more convenient to use pixel sizes instead of handling
         *  with decimal values like 0.01111 to achieve 12 pixels on a 1080-device and 16 pixels on a 1440-device.
         *
         *  Calculation:
         *
         *  final_pixel_size = target_display_min_size * (input_size / reference_size)
         *
         *  Examples:
         *
         *  (reference_size: 1080.0) target_display_min_size: 1440 pixels -> input_size: 540 pixels = 50% = 720 final_pixels
         *  (reference_size: 1080.0) target_display_min_size: 1080 pixels -> input_size: 540 pixels = 50% = 540 final_pixels
         *  (reference_size: 1080.0) target_display_min_size:  720 pixels -> input_size: 540 pixels = 50% = 360 final_pixels
         */
        var REFERENCE_SIZE = 1080.0F
        
        /**
         * By default the UI will scale your layout linearly to match the display resolution of the respective device.
         * But with the base_width_cm this linear scaling can be limited to a specific value. This will prevent your
         * layout from begin scaled up too much, and enables you to use the extra space on larger devices like tablets.
         * Recommended values can probably reach from 7.0 to 9.0. It's also recommended to test the layout of your app
         * on different device resolutions.
         */
        var BASE_WIDTH_CENTIMETERS = 80.0F
        
        /** Diagonal dimension of the display in centimeters to identify large tablets (11"). */
        var LARGE_DISPLAY_DIAGONAL_CM = 24.0F
        
        // ----------------------------------------
        // Constants (Gravity)
        
        const val LEFT           = Gravity.START
        const val CENTER_X       = Gravity.CENTER_HORIZONTAL
        const val RIGHT          = Gravity.END
        const val TOP            = Gravity.TOP
        const val CENTER_Y       = Gravity.CENTER_VERTICAL
        const val BOTTOM         = Gravity.BOTTOM
        
        const val LEFT_TOP       = Gravity.START or Gravity.TOP
        const val LEFT_CENTER    = Gravity.START or Gravity.CENTER_VERTICAL
        const val LEFT_BOTTOM    = Gravity.START or Gravity.BOTTOM
        const val CENTER_TOP     = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        const val CENTER         = Gravity.CENTER
        const val CENTER_BOTTOM  = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        const val RIGHT_TOP      = Gravity.END or Gravity.TOP
        const val RIGHT_CENTER   = Gravity.END or Gravity.CENTER_VERTICAL
        const val RIGHT_BOTTOM   = Gravity.END or Gravity.BOTTOM
        
        // ----------------------------------------
        // Constants (UI Rect)
        
        const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        
        val ZERO         = UIRect.ZERO
        val WRAP         = UIRect.WRAP
        val MATCH        = UIRect.MATCH
        val WRAP_MATCH   = UIRect.WRAP_MATCH
        val MATCH_WRAP   = UIRect.MATCH_WRAP
        val EQUAL_DIST_X = UIRect.EQUAL_DIST_X
        val EQUAL_DIST_Y = UIRect.EQUAL_DIST_Y
        
        // ----------------------------------------
        // Constants (Private)
        
        private const val MIN_SCALE = 0.5F
        private const val MIN_RATIO = 1920F / 1080F
        
        // ----------------------------------------
        // Members
        
        /**
         * Scaling based on the minimum display size, but limited to a physical base width.
         * This prevents the UI from being scaled too much on larger devices like big
         * smartphones and tablets. Usually this is the preferred mode that scales well
         * with smartphones and takes advantage of the extra available space on tablets .
         */
        val limited = UIMethod.Default()
        
        /**
         * Scaling based on the minimum display size. Scales of 1.0 will always result in the
         * minimum display size. Therefore the layout will scale linear on all devices.
         */
        val linear = UIMethod.Default()
        
        // ----------------------------------------
        // Update
        
        /** Updates the UI metrics and returns 'true' if the screen orientation has changed. */
        fun update(context: Context): Boolean
        {
            val metrics = UIMetrics.shared
            
            if (metrics.setup(context))
            {
                var scale = 1.0F
                
                if (metrics.minDisplayCM > BASE_WIDTH_CENTIMETERS) {
                    scale = max(BASE_WIDTH_CENTIMETERS / metrics.minDisplayCM, MIN_SCALE);
                }
                
                if (metrics.displayRatio < MIN_RATIO && metrics.displayRatio / MIN_RATIO < scale) {
                    scale = metrics.displayRatio / MIN_RATIO;
                }
                
                this.limited.size = metrics.minDisplaySize.toFloat() * scale
                this.linear .size = metrics.minDisplaySize.toFloat()
                
                return true
            }
            
            return false
        }
        
        // ----------------------------------------
        // Methods
        
        /** Returns 'true' if the device is currently in portrait orientation. */
        val portrait
            get() = UIMetrics.shared.portrait
        
        /** Returns 'true' if the device is currently in landscape orientation. */
        val landscape
            get() = UIMetrics.shared.landscape
        
        /** Display width in pixels. */
        val displayWidth
            get() = UIMetrics.shared.displayW
        
        /** Display height in pixels. */
        val displayHeight
            get() = UIMetrics.shared.displayH
        
        /** Minimum display size in pixels. */
        val minDisplaySize
            get() = UIMetrics.shared.minDisplaySize
        
        /** Maximum display size in pixels. */
        val maxDisplaySize
            get() = UIMetrics.shared.maxDisplaySize
        
        /** Returns 'true' if the display is around 11" (inch). */
        val isLargeTablet
            get() = UIMetrics.shared.diagonalCM >= LARGE_DISPLAY_DIAGONAL_CM
        
        // ----------------------------------------
        // Edge-To-Edge
        
        @ChecksSdkIntAtLeast(api = 35)
        fun isEdgeToEdgeEnforced(): Boolean =
            android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        
        /** Request the system bar insets for edge-to-edge layouts. */
        fun requestInsets(callback: (Insets) -> Unit) =
            Dash.requestInsets(callback)
        
        /** Request the system bar insets for edge-to-edge layouts. */
        fun requestInsets(receiver: InsetRequest.Receiver) =
            Dash.requestInsets(receiver)
        
        // ----------------------------------------
        // Scaling
        
        /** The scaled result will be rounded down to the lower value. */
        fun floor(scale: Float) = this.limited.floor(scale)
        
        /** The scaled result will be rounded down to the lower value. (Relative to the reference size) */
        fun floor(size: Int) = this.limited.floor(size)
        
        /** The scaled result will be rounded to the nearest value. */
        fun scale(scale: Float) = this.limited.scale(scale)
        
        /** The scaled result will be rounded to the nearest value. (Relative to the reference size) */
        fun scale(size: Int) = this.limited.scale(size)
        
        /** The scaled result will be rounded up, if necessary, to achieve an even value. */
        fun even(scale: Float) = this.limited.even(scale)
        
        /** The scaled result will be rounded up, if necessary, to achieve an even value. (Relative to the reference size) */
        fun even(size: Int) = this.limited.even(size)
        
        // ----------------------------------------
        // Measure Spec
        
        fun measureSpecToString(spec: Int): String
        {
            val mode = View.MeasureSpec.getMode(spec);
            val size = View.MeasureSpec.getSize(spec);
            
            return when (mode) {
                View.MeasureSpec.AT_MOST -> "[AT_MOST: $size]"
                View.MeasureSpec.EXACTLY -> "[EXACTLY: $size]"
                else -> "[UNSPECIFIED]"
            }
        }
    }
}