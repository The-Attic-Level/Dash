package com.the_attic_level.dash.ui.painter

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Xfermode
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.MetricAffectingSpan
import android.view.Gravity
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.ui.layout.font.UIFont
import com.the_attic_level.dash.ui.layout.res.UIColor
import com.the_attic_level.dash.ui.layout.res.UIR
import com.the_attic_level.dash.ui.layout.type.AlignX
import com.the_attic_level.dash.ui.layout.type.AlignY
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.layout.type.UIText
import com.the_attic_level.dash.ui.painter.icon.Icon
import com.the_attic_level.dash.ui.painter.icon.IconPainter
import com.the_attic_level.dash.ui.painter.shape.UIShape

class UIPainter
{
    // ----------------------------------------
    // Static
    
    companion object
    {
        // ------------------------------------
        // Color Filter
        
        val BACKGROUND_MODE: Xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        
        fun createColorFilter(color: Int): ColorFilter {
            return LightingColorFilter(color, 0)
        }
        
        // ------------------------------------
        // Thread Safe Instances
        
        private val shared = ArrayList<UIPainter>(2)
        private val main = UIPainter()
        
        private val instance: UIPainter; get()
        {
            // in most cases we'll use the same painter
            if (Dash.onMainThread() && this.main.available) {
                this.main.busy = true
                return this.main
            }
            
            synchronized(this.shared)
            {
                for (painter in this.shared) {
                    if (painter.available) {
                        painter.busy = true
                        return painter
                    }
                }
                
                val painter = UIPainter()
                painter.busy = true
                
                if (this.shared.size < 64) {
                    this.shared.add(painter)
                } else {
                    Logger.warn(UIPainter::class, "painter overflow")
                }
                
                return painter
            }
        }
        
        // ------------------------------------
        // Create (Bitmap) / Draw
        
        fun create(width: Int, height: Int, config: Config, action: (UIPainter) -> Unit): Bitmap =
            Bitmap.createBitmap(width, height, config).also { this.instance.draw(it, action) }
        
        fun draw(bitmap: Bitmap, action: (UIPainter) -> Unit) =
            this.instance.draw(bitmap, action)
        
        fun draw(canvas: Canvas, view: View, action: (UIPainter) -> Unit) =
            draw(canvas, view.width, view.height, action)
        
        fun draw(canvas: Canvas, drawable: Drawable, action: (UIPainter) -> Unit) =
            draw(canvas, drawable.bounds, action)
        
        fun draw(canvas: Canvas, bounds: Rect, action: (UIPainter) -> Unit) =
            draw(canvas, bounds.width(), bounds.height(), action)
        
        fun draw(canvas: Canvas, width: Int, height: Int, action: (UIPainter) -> Unit) =
            this.instance.draw(canvas, width, height, action)
    }
    
    // ----------------------------------------
    // Members (Private)
    
    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val point = Point()
    private val rect  = Rect()
    private val box   = UIBox()
    
    private var busy = false
    private var tmpColor: Int = 0
    
    var canvas: Canvas? = null
        private set
    
    private val internalCanvas: Canvas by lazy {
        Canvas()
    }
    
    // ----------------------------------------
    // Members
    
    val path: UIPathPainter by lazy {
        UIPathPainter(this)
    }
    
    var width   = 0; private set
    var height  = 0; private set
    var gravity = Gravity.CENTER
    
    var alignIconToSize = false
    
    // ----------------------------------------
    // Properties
    
    val available: Boolean
        get() = !this.busy
    
    var color: Int
        get() = this.paint.color
        set(value) {
            this.paint.color = value
        }
    
    var shader: Shader
        get() = this.paint.shader
        set(value) {
            this.paint.shader = value
        }
    
    var textSize: Int
        get() = this.paint.textSize.toInt()
        set(value) {
            this.paint.textSize = value.toFloat()
        }
    
    // ----------------------------------------
    // Init
    
    init {
        this.paint.color = Color.WHITE
        this.paint.style = Paint.Style.FILL
    }
    
    // ----------------------------------------
    // Drawing
    
    fun draw(bitmap: Bitmap, action: (UIPainter) -> Unit) {
        val canvas = this.internalCanvas
        canvas.setBitmap(bitmap)
        draw(canvas, bitmap.width, bitmap.height, action)
        canvas.setBitmap(null)
    }
    
    fun draw(canvas: Canvas, width: Int, height: Int, action: (UIPainter) -> Unit)
    {
        this.busy   = true
        this.canvas = canvas
        this.width  = width
        this.height = height
        
        reset()
        action(this)
        
        this.canvas = null
        this.width  = 0
        this.height = 0
        this.busy   = false
    }
    
    fun reset()
    {
        this.tmpColor = 0
        this.gravity  = Gravity.CENTER
        
        this.alignIconToSize = false
        
        if (this.paint.color != Color.WHITE) {
            this.paint.color = Color.WHITE
        }
        
        if (this.paint.style != Paint.Style.FILL) {
            this.paint.style = Paint.Style.FILL
        }
        
        if (this.paint.shader != null) {
            this.paint.shader = null
        }
        
        if (this.paint.xfermode != null) {
            this.paint.xfermode = null
        }
        
        if (this.paint.colorFilter != null) {
            this.paint.colorFilter = null
        }
    }
    
    // ----------------------------------------
    // Methods
    
    fun fill() {
        this.paint.style = Paint.Style.FILL
    }
    
    fun stroke(width: Int) {
        stroke(width.toFloat())
    }
    
    fun stroke(width: Float) {
        this.paint.style = Paint.Style.STROKE
        this.paint.strokeWidth = width
    }
    
    fun color(res: UIR) {
        this.paint.color = res.color
    }
    
    fun color(color: Int) {
        this.paint.color = color
    }
    
    fun alpha(@IntRange(from = 0, to = 255) alpha: Int) {
        this.paint.color = UIColor.applyAlpha(this.paint.color, alpha)
    }
    
    fun alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        this.paint.color = UIColor.applyAlpha(this.paint.color, alpha)
    }
    
    fun foreground() {
        this.paint.xfermode = null
        this.paint.color = if (this.tmpColor != 0) this.tmpColor else Color.WHITE
    }
    
    fun background() {
        this.tmpColor = this.paint.color
        this.paint.xfermode = BACKGROUND_MODE
        this.paint.color = Color.TRANSPARENT
    }
    
    fun applyColorFilter() {
        this.tmpColor = this.paint.color
        this.paint.xfermode = null
        this.paint.color = this.tmpColor or 0x00FFFFFF
        this.paint.colorFilter = createColorFilter(this.tmpColor)
    }
    
    fun clearColorFilter() {
        this.paint.colorFilter = null
        foreground()
    }
    
    fun font(font: UIFont) {
        this.paint.typeface = font.typeface
    }
    
    fun setup(text: UIText) {
        this.paint.typeface = text.font.typeface
        this.paint.color    = text.color.color
        this.paint.textSize = text.size.toFloat()
        this.gravity        = text.gravity
    }
    
    // ----------------------------------------
    // Draw Shapes
    
    fun rect(box: UIBox) = rect(box.x, box.y, box.w, box.h)
    fun rect(x: Int, y: Int, w: Int, h: Int) = rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat())
    fun rect(x: Float, y: Float, w: Float, h: Float) = this.canvas?.drawRect(x, y, x+w, y+h, this.paint)
    
    fun line(x1: Int, y1: Int, x2: Int, y2: Int) {
        this.canvas?.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), this.paint)
    }
    
    fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
        this.canvas?.drawLine(x1, y1, x2, y2, this.paint)
    }
    
    fun circle(x: Int, y: Int, radius: Int) {
        this.canvas?.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), this.paint)
    }
    
    fun circle(x: Float, y: Float, radius: Float) {
        this.canvas?.drawCircle(x, y, radius, this.paint)
    }
    
    fun ellipse(x: Float, y: Float, radiusA: Float, radiusB: Float) {
        this.canvas?.drawOval(x-radiusA, y-radiusB, x+radiusA, y+radiusB, this.paint)
    }
    
    fun arc(x: Float, y: Float, radius: Float, start: Float, sweep: Float) {
        this.canvas?.drawArc(x-radius, y-radius, x+radius, y+radius, start, sweep, false, this.paint)
    }
    
    fun path(path: Path) {
        this.canvas?.drawPath(path, this.paint)
    }
    
    fun shape(shape: UIShape, box: UIBox) {
        shape.draw(this, box)
    }
    
    fun shape(shape: UIShape, x: Int, y: Int, w: Int, h: Int) {
        this.box.set(x, y, w, h)
        shape.draw(this, this.box)
    }
    
    // ----------------------------------------
    // Draw Frames
    
    fun frame(box: UIBox, stroke: Int) {
        frame(box.x, box.y, box.w, box.h, stroke)
    }
    
    fun frame(x: Int, y: Int, w: Int, h: Int, stroke: Int)
    {
        val r = x + w - stroke
        val b = y + h - stroke
        
        this.path.begin()
        this.path.rect(x, y, x + w, y + stroke)
        this.path.rect(x, b, x + w, y + h)
        this.path.rect(x, y + stroke, x + stroke, b)
        this.path.rect(r, y + stroke, x + w, b)
        this.path.draw()
    }
    
    // ----------------------------------------
    // Draw Rounded Rectangles
    
    fun rounded(box: UIBox, radius: Int) {
        rounded(box.x.toFloat(), box.y.toFloat(), box.w.toFloat(), box.h.toFloat(), radius.toFloat())
    }
    
    fun rounded(x: Int, y: Int, w: Int, h: Int, radius: Int) {
        rounded(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), radius.toFloat())
    }
    
    fun rounded(x: Float, y: Float, w: Float, h: Float, radius: Float) {
        if (radius > 0) {
            this.canvas?.drawRoundRect(x, y, x+w, y+h, radius, radius, this.paint)
        } else {
            this.canvas?.drawRect(x, y, x+w, y+h, this.paint)
        }
    }
    
    fun rounded(box: UIBox, radius: Int, stroke: Int) {
        rounded(box.x, box.y, box.w, box.h, radius, stroke)
    }
    
    fun rounded(x: Int, y: Int, w: Int, h: Int, radius: Int, stroke: Int)
    {
        val path = this.path
        
        // top: left corner + center bar + right corner
        path.begin()
        path.arc(x+radius,   y+radius, radius,        180.0F,   90.0F)
        path.arc(x+w-radius, y+radius, radius,        270.0F,   90.0F)
        path.arc(x+w-radius, y+radius, radius-stroke,   0.0F, - 90.0F)
        path.arc(x+radius,   y+radius, radius-stroke, 270.0F, - 90.0F)
        path.draw()
        
        // bottom: right corner - center bar + left corner
        path.begin()
        path.arc(x+w-radius, y+h-radius, radius,          0.0F,   90.0F)
        path.arc(x+radius,   y+h-radius, radius,         90.0F,   90.0F)
        path.arc(x+radius,   y+h-radius, radius-stroke, 180.0F, - 90.0F)
        path.arc(x+w-radius, y+h-radius, radius-stroke,  90.0F, - 90.0F)
        path.draw()
        
        // left and right center bars
        if (h - 2 * radius > 0) {
            path.begin()
            path.rect(x,          y+radius, stroke, h-2*radius)
            path.rect(x+w-stroke, y+radius, stroke, h-2*radius)
            path.draw()
        }
    }
    
    // ----------------------------------------
    // Draw Icons
    
    fun draw(icon: Icon, x: Int, y: Int, w: Int, h: Int, scale: Float) {
        this.box.set(x, y, w, h)
        draw(icon, this.box, IconPainter.calculateSize(box, scale))
    }
    
    fun draw(icon: Icon, x: Int, y: Int, w: Int, h: Int, size: Int) {
        this.box.set(x, y, w, h)
        draw(icon, this.box, size)
    }
    
    fun draw(icon: Icon, box: UIBox, scale: Float) {
        draw(icon, box, IconPainter.calculateSize(box, scale))
    }
    
    fun draw(icon: Icon, box: UIBox, size: Int)
    {
        val color = this.paint.color
        
        // check if color is visible
        if (color ushr 24 > 0)
        {
            val filter = this.paint.colorFilter
            val bitmap = icon.getBitmap(size) ?: return
            
            if (color != Color.WHITE) {
                this.paint.color = color or 0x00FFFFFF
                this.paint.colorFilter = createColorFilter(color)
            } else if (this.paint.colorFilter != null) {
                this.paint.colorFilter = null
            }
            
            // calculate icon position
            IconPainter.setup(box, size, bitmap, this.gravity, this.point, this.alignIconToSize)
            
            val x = this.point.x.toFloat()
            val y = this.point.y.toFloat()
            
            // draw icon bitmap
            this.canvas?.drawBitmap(bitmap, x, y, this.paint)
            
            if (this.paint.color != color) {
                this.paint.color = color
            }
            
            if (this.paint.colorFilter != filter) {
                this.paint.colorFilter = filter
            }
        }
    }
    
    // ----------------------------------------
    // Draw Bitmaps
    
    fun draw(bitmap: Bitmap, x: Int, y: Int) {
        this.canvas?.drawBitmap(bitmap, x.toFloat(), y.toFloat(), this.paint)
    }
    
    fun draw(bitmap: Bitmap, x: Float, y: Float) {
        this.canvas?.drawBitmap(bitmap, x, y, this.paint)
    }
    
    fun draw(bitmap: Bitmap, src: Rect, dst: Rect) {
        this.canvas?.drawBitmap(bitmap, src, dst, this.paint)
    }
    
    fun draw(bitmap: Bitmap, matrix: Matrix) {
        this.canvas?.drawBitmap(bitmap, matrix, this.paint)
    }
    
    // ----------------------------------------
    // Text Measure
    
    val verticalTextCenter: Float
        get() = (this.paint.descent() + this.paint.ascent()) / 2
    
    fun bounds(text: String, rect: Rect) {
        this.paint.getTextBounds(text, 0, text.length, rect)
    }
    
    fun measure(text: CharSequence): Float {
        return this.paint.measureText(text, 0, text.length)
    }
    
    fun measure(text: CharSequence, start: Int, end: Int): Float {
        return this.paint.measureText(text, start, end)
    }
    
    fun measureBounds(text: String, rect: Rect) {
        measureBounds(text, 0, text.length, rect)
    }
    
    fun measureBounds(text: String, start: Int, end: Int, rect: Rect) {
        this.paint.getTextBounds(text, start, end, rect)
    }
    
    fun measure(spannable: SpannableString): Float
    {
        // save current text size to restore later
        val textSize = this.paint.textSize
        
        // total width of the spannable text
        var width = 0.0F
        
        var i = 0
        var n: Int
        
        while (i < spannable.length)
        {
            // find the next span transition
            n = spannable.nextSpanTransition(i, spannable.length, CharacterStyle::class.java)
            
            // get metric adjust spans
            val spans = spannable.getSpans(i, n, MetricAffectingSpan::class.java)
            
            // apply metric adjustments
            if (spans != null && spans.isNotEmpty()) {
                spans[0].updateMeasureState(paint)
            }
            
            // advance text position
            width += paint.measureText(spannable, i, n)
            
            // restore baseline shift
            this.paint.baselineShift = 0
            
            // restore text size
            this.paint.textSize = textSize
            i = n
        }
        
        return width
    }
    
    // ----------------------------------------
    // Draw Text
    
    fun drawCentered(text: String, x: Int, y: Int) =
        drawCentered(text, x.toFloat(), y.toFloat())
    
    fun drawCentered(text: String, x: Float, y: Float)
    {
        var px = x
        var py = y
        
        this.paint.textAlign = Paint.Align.LEFT
        this.paint.getTextBounds(text, 0, text.length, this.rect)
        
        px -= this.rect.exactCenterX()
        py -= this.rect.exactCenterY()
        
        this.canvas?.drawText(text, px, py, paint)
    }
    
    fun draw(text: CharSequence, x: Int, y: Int) {
        draw(text, x.toFloat(), y.toFloat(), 0.0F, 0.0F)
    }
    
    fun draw(text: CharSequence, box: UIBox) {
        draw(text, box.x.toFloat(), box.y.toFloat(), box.w.toFloat(), box.h.toFloat())
    }
    
    fun draw(text: CharSequence, x: Float, y: Float, w: Float, h: Float)
    {
        var px = x
        var py = y
        
        if (text.isNotEmpty())
        {
            val alignY = AlignY.get(this.gravity)
            val alignX = AlignX.get(this.gravity)
            
            if (alignY == AlignY.CENTER) {
                py += h / 2 - this.verticalTextCenter
            } else if (alignY == AlignY.BOTTOM) {
                py += h - this.paint.fontSpacing
            }
            
            if (text is SpannableString) {
                if (alignX == AlignX.CENTER) {
                    px += (w - measure(text)) / 2.0F
                } else if (alignX == AlignX.RIGHT) {
                    px += w - measure(text)
                }
                draw(text, px, py)
            } else {
                val e = text.length
                if (alignX == AlignX.CENTER) {
                    px += (w - this.paint.measureText(text, 0, e)) / 2.0F
                } else if (alignX == AlignX.RIGHT) {
                    px += w - this.paint.measureText(text, 0, e)
                }
                this.canvas?.drawText(text, 0, e, px, py, paint)
            }
        }
    }
    
    // ----------------------------------------
    // Draw Text (Private)
    
    private fun draw(spannable: SpannableString, x: Float, y: Float)
    {
        // save current text size to restore later
        val textSize = this.paint.textSize
        
        var p = x
        var i = 0
        var n: Int
        
        while (i < spannable.length)
        {
            // find the next span transition
            n = spannable.nextSpanTransition(i, spannable.length, CharacterStyle::class.java)
            
            // get metric adjust spans
            val spans = spannable.getSpans(i, n, MetricAffectingSpan::class.java)
            
            // apply metric adjustments
            if (spans != null && spans.isNotEmpty()) {
                spans[0].updateDrawState(paint)
            }
            
            // draw text
            this.canvas?.drawText(spannable, i, n, p, y + this.paint.baselineShift, paint)
            
            // advance text position
            p += this.paint.measureText(spannable, i, n)
            
            // restore baseline shift
            this.paint.baselineShift = 0
            
            // restore text size
            this.paint.textSize = textSize
            i = n
        }
    }
}