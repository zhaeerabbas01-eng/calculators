package com.example.domain.engine

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

object GeometryEngine {
    
    sealed class ShapeResult {
        data class TwoD(val area: Double, val perimeter: Double) : ShapeResult()
        data class ThreeD(val volume: Double, val surfaceArea: Double) : ShapeResult()
    }

    // 2D Shapes
    fun calculateSquare(side: Double): ShapeResult.TwoD {
        return ShapeResult.TwoD(side * side, 4 * side)
    }

    fun calculateRectangle(length: Double, width: Double): ShapeResult.TwoD {
        return ShapeResult.TwoD(length * width, 2 * (length + width))
    }

    fun calculateCircle(radius: Double): ShapeResult.TwoD {
        return ShapeResult.TwoD(PI * radius.pow(2), 2 * PI * radius)
    }

    fun calculateTriangle(base: Double, height: Double, side1: Double, side2: Double, side3: Double): ShapeResult.TwoD {
        val area = 0.5 * base * height
        val perimeter = side1 + side2 + side3
        return ShapeResult.TwoD(area, perimeter)
    }
    
    fun calculateRightTriangle(base: Double, height: Double): ShapeResult.TwoD {
        val hypotenuse = sqrt(base.pow(2) + height.pow(2))
        return ShapeResult.TwoD(0.5 * base * height, base + height + hypotenuse)
    }

    // 3D Shapes
    fun calculateCube(side: Double): ShapeResult.ThreeD {
        return ShapeResult.ThreeD(side.pow(3), 6 * side.pow(2))
    }
    
    fun calculateCylinder(radius: Double, height: Double): ShapeResult.ThreeD {
        val volume = PI * radius.pow(2) * height
        val surfaceArea = 2 * PI * radius * (radius + height)
        return ShapeResult.ThreeD(volume, surfaceArea)
    }
    
    fun calculateSphere(radius: Double): ShapeResult.ThreeD {
        val volume = (4.0 / 3.0) * PI * radius.pow(3)
        val surfaceArea = 4 * PI * radius.pow(2)
        return ShapeResult.ThreeD(volume, surfaceArea)
    }
}
