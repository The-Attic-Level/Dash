package com.the_attic_level.dash.ui.painter.icon

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.Gravity
import android.view.View
import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.layout.res.UIColorList
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.AlignX
import com.the_attic_level.dash.ui.layout.type.AlignY
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter
import kotlin.math.min

open class IconPainter(private val parent: View? = null)
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        private val DEFAULT_STATE = intArrayOf(UIColorList.STATE_ENABLED)
        private const val DEFAULT_COLOR = Color.MAGENTA
    
        fun calculateSize(box: UIBox, scale: Float): Int {
            return UIMath.round(min(box.w, box.h), scale)
        }
        
        fun setup(box: UIBox, size: Int, bitmap: Bitmap, gravity: Int, out: Point, alignToSize: Boolean = false)
        {
            val alignX = AlignX.get(gravity)
            val alignY = AlignY.get(gravity)
            
            out.x = box.x
            out.y = box.y
            
            // calculate horizontal position
            if (alignX == AlignX.CENTER) {
                out.x += (box.w - bitmap.width) / 2
            } else if (alignX == AlignX.RIGHT) {
                out.x += box.w - bitmap.width
                if (alignToSize && bitmap.width < size) {
                    out.x -= (size - bitmap.width) / 2
                }
            } else if (alignToSize && bitmap.width < size) {
                out.x += (size - bitmap.width) / 2
            }
            
            // calculate vertical position
            if (alignY == AlignY.CENTER) {
                out.y += (box.h - bitmap.height) / 2
            } else if (alignY == AlignY.BOTTOM) {
                out.y += box.h - bitmap.height
                if (alignToSize && bitmap.height < size) {
                    out.y -= (size - bitmap.height) / 2
                }
            } else if (alignToSize && bitmap.height < size) {
                out.y += (size - bitmap.height) / 2
            }
        }
    }
    
    // ----------------------------------------
    // Members (Final)
    
    private val paint = Paint()
    private val point = Point()
    private val box   = UIBox()
    
    // ----------------------------------------
    // Members (Public)
    
    var icon: Icon? = null
        set(value) {
            if (this.icon !== value) {
                field = value
                this.bitmap = null
                this.bitmapSize = 0
                this.parent?.invalidate()
            }
        }
    
    var gravity = Gravity.CENTER
        set(value) {
            if (this.gravity != value) {
                field = value
                this.parent?.invalidate()
            }
        }
    
    var alignToSize = false
        set(value) {
            if (this.alignToSize != value) {
                field = value
                this.parent?.invalidate()
            }
        }
    
    // ----------------------------------------
    // Properties
    
    var size: Int
        get() = this.internalSize
        set(value) {
            if (this.internalSize != value) {
                this.internalSize  = value
                this.internalScale = 0.0F
                this.parent?.invalidate()
            }
        }
    
    var scale: Float
        get() = this.internalScale
        set(value) {
            if (this.internalScale != value) {
                this.internalScale = value
                this.internalSize  = 0
                this.parent?.invalidate()
            }
        }
    
    var color: Int
        get() = this.internalColor
        set(value) {
            if (this.internalColor != value) {
                this.internalColor = value
                this.internalRes   = null
                this.parent?.invalidate()
            }
        }
    
    var res: UIR?
        get() = this.internalRes
        set(value) {
            if (this.internalRes !== value) {
                this.internalRes = value
                this.parent?.invalidate()
            }
        }
    
    val isStateful: Boolean
        get() = this.res?.type == UIR.Type.COLOR_LIST
    
    // ----------------------------------------
    // Members (Private)
    
    private var internalSize  : Int   = 0
    private var internalScale : Float = 0.35F
    private var internalColor : Int   = Color.WHITE
    private var internalRes   : UIR?  = null
    
    private var bitmap: Bitmap? = null
    private var bitmapSize = 0
    private var paintColor = 0
    
    // ----------------------------------------
    // Methods
    
    fun draw(canvas: Canvas, x: Int, y: Int, w: Int, h: Int) {
        this.box.set(x, y, w, h)
        draw(canvas, this.box)
    }
    
    fun draw(canvas: Canvas, box: UIBox)
    {
        val icon = this.icon ?: return
        
        // calculate bitmap size
        val size = if (this.internalScale > 0.0F) {
            calculateSize(box, this.internalScale)
        } else {
            this.internalSize
        }
        
        if (size <= 0) {
            return
        }
        
        // get icon bitmap
        if (this.bitmap == null || this.bitmapSize != size) {
            this.bitmap = icon.getBitmap(size)
            this.bitmapSize = size
        }
        
        val bitmap = this.bitmap ?: return
        val color  = this.currentColor
        
        // check if color is visible
        if (color ushr 24 > 0)
        {
            if (this.paintColor != color)
            {
                this.paintColor = color
                
                if (color != Color.WHITE) {
                    this.paint.color = color or 0x00FFFFFF
                    this.paint.colorFilter = UIPainter.createColorFilter(color)
                } else {
                    this.paint.color = Color.WHITE
                    this.paint.colorFilter = null
                }
            }
            
            // calculate icon position
            setup(box, size, bitmap, this.gravity, this.point, this.alignToSize)
            
            val x = this.point.x.toFloat()
            val y = this.point.y.toFloat()
            
            // draw icon bitmap
            canvas.drawBitmap(bitmap, x, y, this.paint)
        }
    }
    
    // ----------------------------------------
    // Methods (Private)
    
    private val drawableState: IntArray
        get() = this.parent?.drawableState ?: DEFAULT_STATE
    
    private val currentColor: Int; get()
    {
        if (this.internalRes == null) {
            return this.internalColor
        }
        
        val resource = this.internalRes ?: return DEFAULT_COLOR
        
        if (resource.type == UIR.Type.COLOR_LIST) {
            return resource.color(this.drawableState)
        }
        
        return resource.color
    }
}