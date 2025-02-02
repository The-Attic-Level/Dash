package com.the_attic_level.dash.ui.painter.shape

import com.the_attic_level.dash.ui.layout.UIMath
import com.the_attic_level.dash.ui.layout.type.UIBounds
import com.the_attic_level.dash.ui.layout.type.UIBox
import com.the_attic_level.dash.ui.painter.UIPainter
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class UIPolygon(var radius: Float, var points: Int, var degrees: Double, var detail: Float, var miter: Miter): UIShape
{
    // ----------------------------------------
    // Enum
    
    enum class Miter
    {
        /*
         * the shape will maintain the polygons bounds, but will be slightly smaller
         * (relative to the polygon without the rounded corners)
         */
        MAINTAIN_BOUNDS,
        
        /*
         * the shape will maintain the polygons tips, but will be slightly bigger
         * (relative to the polygon without the rounded corners)
         */
        MAINTAIN_TIPS
    }
    
    // ----------------------------------------
    // UI Shape
    
    override fun draw(painter: UIPainter, box: UIBox) {
        draw(painter, box.cx.toFloat(), box.cy.toFloat())
    }
    
    // ----------------------------------------
    // Draw
    
    fun draw(painter: UIPainter, x: Int, y: Int) {
        draw(painter, x.toFloat(), y.toFloat())
    }
    
    fun draw(painter: UIPainter, x: Float, y: Float)
    {
        val angleStepDeg = calculateAngleStepDeg(this.points)
        val detailRadius = calculateDetailRadius(this.radius, this.detail)
        
        var px: Float
        var py: Float
        var angleRad: Double
        
        // create and draw path
        
        val path = painter.path
        path.begin()
        
        if (detailRadius > 0)
        {
            // draw with rounded corners
            
            var angleDeg: Double
            val invertRadius = calculateInvertRadius(angleStepDeg, this.radius, detailRadius, this.miter)
            
            for (i in 0 until this.points)
            {
                angleDeg = this.degrees + i.toDouble() * angleStepDeg
                angleRad = Math.toRadians(angleDeg)
                
                px = x + UIMath.round(cos(angleRad) * invertRadius)
                py = y + UIMath.round(sin(angleRad) * invertRadius)
                
                path.arc(px, py, detailRadius, angleDeg.toFloat() - angleStepDeg / 2.0F, angleStepDeg)
            }
        }
        else
        {
            val radius = this.radius.toDouble()
            
            // draw without rounded corners
            for (i in 0 until this.points)
            {
                angleRad = Math.toRadians(this.degrees + i.toDouble() * angleStepDeg)
                
                px = x + UIMath.round(cos(angleRad) * radius)
                py = y + UIMath.round(sin(angleRad) * radius)
                
                path.line(px, py)
            }
        }
    
        path.draw()
    }
    
    // ----------------------------------------
    // Bounds
    
    fun bounds(x: Float, y: Float, bounds: UIBounds)
    {
        val angleStepDeg = calculateAngleStepDeg(this.points)
        val detailRadius = calculateDetailRadius(this.radius, this.detail)
        
        var px: Float
        var py: Float
        var radians: Double
        
        if (this.detail > 0.0F)
        {
            val invertRadius = calculateInvertRadius(angleStepDeg, this.radius, detailRadius, this.miter)
            
            for (i in 0 until this.points)
            {
                radians = Math.toRadians(this.degrees + i.toDouble() * angleStepDeg)
                
                px = x + UIMath.round(cos(radians) * invertRadius)
                py = y + UIMath.round(sin(radians) * invertRadius)
                
                bounds.update(px - detailRadius, py - detailRadius)
                bounds.update(px - detailRadius, py + detailRadius)
                bounds.update(px + detailRadius, py - detailRadius)
                bounds.update(px + detailRadius, py + detailRadius)
            }
        }
        else
        {
            val radius = this.radius.toDouble()
            
            for (i in 0 until this.points)
            {
                radians = Math.toRadians(this.degrees + i.toDouble() * angleStepDeg)
                
                px = x + UIMath.round(cos(radians) * radius)
                py = y + UIMath.round(sin(radians) * radius)
                
                bounds.update(px, py)
            }
        }
    }
    
    // ----------------------------------------
    // Calculate
    
    private fun calculateDetailRadius(radius: Float, detail: Float): Float {
        return UIMath.round(radius * detail).toFloat()
    }
    
    private fun calculateAngleStepDeg(points: Int): Float {
        return (36_000.0F / points.toFloat()).roundToInt() / 100.0F
    }
    
    private fun calculateInvertRadius(angleStep: Float, radius: Float, detailRadius: Float, miter: Miter): Float
    {
        if (miter == Miter.MAINTAIN_TIPS) {
            return radius - detailRadius
        }
        
        // calculates a distance from the center (radius) so that the detail radius only touches the shapes boundaries
        val sinC = sin(Math.toRadians(angleStep / 2.0)) // sinus of angle between hypotenuse and 'detail radius'
        val sinB = sin(Math.toRadians(90.0 - angleStep / 2.0)) // sinus of angle between hypotenuse and 'side'
        
        // calculates the other side of the right-angled-triangle - with this we can calculate the hypotenuse
        val side = sinC / sinB * detailRadius.toDouble()
        
        // calculates the hypotenuse of the right-angled-triangle
        val hypotenuse = sqrt(detailRadius.toDouble() * detailRadius.toDouble() + side * side)
        
        // subtracts the hypotenuse from the radius to get the invert radius
        return radius - hypotenuse.toFloat()
    }
}