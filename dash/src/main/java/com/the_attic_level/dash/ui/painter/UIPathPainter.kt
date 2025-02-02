package com.the_attic_level.dash.ui.painter

import android.graphics.Path
import kotlin.math.sqrt

class UIPathPainter(val painter: UIPainter)
{
    // ----------------------------------------
    // Members (Final)
    
    private val path = Path()
    
    // ----------------------------------------
    // Members
    
    private var move = false
    
    // ----------------------------------------
    // Lines / Curves
    
    fun begin() {
        this.path.reset()
        this.move = true
    }
    
    fun move() {
        this.move = true
    }
    
    fun move(x: Float, y: Float) {
        this.path.moveTo(x, y)
        this.move = false
    }
    
    fun line(x: Float, y: Float) {
        if (this.move) {
            this.move = false
            this.path.moveTo(x, y)
        } else {
            this.path.lineTo(x, y)
        }
    }
    
    fun line(xa: Float, ya: Float, xb: Float, yb: Float) {
        this.path.moveTo(xa, ya)
        this.path.lineTo(xb, yb)
    }
    
    /** start and sweep angle in degrees */
    fun arc(x: Float, y: Float, r: Float, start: Float, sweep: Float) {
        this.path.arcTo(x-r, y-r, x+r, y+r, start, sweep, false)
        this.move = false
    }
    
    fun cubic(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        this.path.cubicTo(x1, y1, x2, y2, x3, y3)
        this.move = false
    }
    
    fun close() {
        this.path.close()
    }
    
    fun draw() {
        this.painter.path(this.path)
    }
    
    // ----------------------------------------
    // Shapes
    
    fun line(xa: Float, ya: Float, xb: Float, yb: Float, sr: Float)
    {
        val dx = xb - xa
        val dy = yb - ya
        val ln = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        
        if (ln != 0.0F)
        {
            val rx = -(dy / ln)
            val ry = dx / ln
            
            move(xa + rx * sr, ya + ry * sr)
            line(xb + rx * sr, yb + ry * sr)
            line(xb - rx * sr, yb - ry * sr)
            line(xa - rx * sr, ya - ry * sr)
            close()
        }
    }
    
    fun circle(x: Float, y: Float, r: Float) {
        this.path.addCircle(x, y, r, Path.Direction.CW)
    }
    
    fun rect(x: Float, y: Float, w: Float, h: Float) {
        this.path.addRect(x, y, x + w, y + h, Path.Direction.CW)
    }
    
    fun rect(x: Float, y: Float, w: Float, h: Float, r: Float) {
        this.path.addRoundRect(x, y, x + w, y + h, r, r, Path.Direction.CW)
    }
    
    // ----------------------------------------
    // Integer Methods
    
    fun move(x: Int, y: Int) = move(x.toFloat(), y.toFloat())
    fun line(x: Int, y: Int) = line(x.toFloat(), y.toFloat())
    
    fun cubic(x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int) =
        cubic(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), x3.toFloat(), y3.toFloat())
    
    fun arc(x: Int, y: Int, r: Int, start: Float, sweep: Float) =
        arc(x.toFloat(), y.toFloat(), r.toFloat(), start, sweep)
    
    fun line(xa: Int, ya: Int, xb: Int, yb: Int) =
        line(xa.toFloat(), ya.toFloat(), xb.toFloat(), yb.toFloat())
    
    fun line(xa: Int, ya: Int, xb: Int, yb: Int, sr: Int) =
        line(xa.toFloat(), ya.toFloat(), xb.toFloat(), yb.toFloat(), sr.toFloat())
    
    fun circle(x: Int, y: Int, r: Int) =
        circle(x.toFloat(), y.toFloat(), r.toFloat())
    
    fun rect(x: Int, y: Int, w: Int, h: Int) =
        rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat())
    
    fun rect(x: Int, y: Int, w: Int, h: Int, r: Int) =
        rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), r.toFloat())
}