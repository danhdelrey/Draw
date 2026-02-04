package com.example.draw.data.model.shape

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Represents the state of a rectangle for the Rectangle Drawing Mode.
 *
 * This is a temporary overlay state, not committed to drawing data
 * until user performs actual drawing action.
 *
 * @property center The center point of the rectangle in canvas coordinates
 * @property width The width of the rectangle
 * @property height The height of the rectangle
 * @property rotation The rotation angle in radians (clockwise)
 */
data class RectangleState(
    val center: Offset,
    val width: Float,
    val height: Float,
    val rotation: Float = 0f
) {
    /**
     * Creates a default rectangle centered at the given position
     */
    companion object {
        private const val DEFAULT_SIZE = 300f

        fun createDefault(canvasWidth: Float, canvasHeight: Float): RectangleState {
            return RectangleState(
                center = Offset(canvasWidth / 2f, canvasHeight / 2f),
                width = DEFAULT_SIZE,
                height = DEFAULT_SIZE,
                rotation = 0f
            )
        }
    }

    /**
     * Check if this is a perfect square
     */
    @Suppress("unused")
    val isSquare: Boolean
        get() = width == height

    /**
     * Project a point onto the rectangle perimeter.
     *
     * @param point The point to project
     * @return The projected point on the rectangle perimeter
     */
    fun projectPointToPerimeter(point: Offset): Offset {
        // Rotate point to local coordinates (unrotated)
        val relativePt = point - center
        val rotatedPt = relativePt.rotate(-rotation)

        val halfWidth = width / 2f
        val halfHeight = height / 2f

        // Check if inside rectangle
        val isInside = rotatedPt.x > -halfWidth && rotatedPt.x < halfWidth &&
                rotatedPt.y > -halfHeight && rotatedPt.y < halfHeight

        val snappedLocalPt: Offset
        if (isInside) {
            // Radial projection from center for smooth corners
            if (rotatedPt == Offset.Zero) {
                // If exactly at center, default to right edge
                snappedLocalPt = Offset(halfWidth, 0f)
            } else {
                // Determine intersection with box border
                // Line from (0,0) to (rx, ry) is: P = t * rotatedPt
                // We want to find t where P hits the boundary.

                // Distances to X edges: halfWidth / abs(rx)
                // Distances to Y edges: halfHeight / abs(ry)

                val tx = if (rotatedPt.x != 0f) halfWidth / abs(rotatedPt.x) else Float.POSITIVE_INFINITY
                val ty = if (rotatedPt.y != 0f) halfHeight / abs(rotatedPt.y) else Float.POSITIVE_INFINITY

                val t = min(tx, ty)
                snappedLocalPt = rotatedPt * t
            }
        } else {
            // Outside: Snap to bounds (orthogonal projection)
            val x = rotatedPt.x.coerceIn(-halfWidth, halfWidth)
            val y = rotatedPt.y.coerceIn(-halfHeight, halfHeight)
            snappedLocalPt = Offset(x, y)
        }

        // Rotate back to global coordinates
        return snappedLocalPt.rotate(rotation) + center
    }

    /**
     * Project a point onto the rectangle perimeter with angle tracking for stability.
     *
     * @param point The point to project
     * @param previousTheta The previous angle (radians) to maintain continuity near center
     * @return Pair of (Projected Point, Current Angle)
     */
    fun projectPointToPerimeterWithAngle(point: Offset, previousTheta: Float?): Pair<Offset, Float> {
        // Rotate point to local coordinates (unrotated)
        val relativePt = point - center
        val rotatedPt = relativePt.rotate(-rotation)

        val halfWidth = width / 2f
        val halfHeight = height / 2f

        val distanceFromCenter = rotatedPt.getDistance()
        val minDistance = min(halfWidth, halfHeight) * 0.3f // 30% of smallest dimension

        val rawTheta = atan2(rotatedPt.y, rotatedPt.x)

        val theta: Float = if (previousTheta == null) {
            rawTheta
        } else if (distanceFromCenter < minDistance) {
            // Point is too close to center, use previous angle for continuity
            // to prevent chaotic jumping when crossing the origin
            previousTheta
        } else {
            // Apply smoothing/clamping if needed, but for rectangle radial projection
            // just using the raw angle is usually fine if we are far from center.
            // We could add the same smooth wrapping logic as EllipseState here if desired.

            var angleDiff = rawTheta - previousTheta
            while (angleDiff > PI) angleDiff -= (2 * PI).toFloat()
            while (angleDiff < -PI) angleDiff += (2 * PI).toFloat()

            // Limit max angle change for smoothness
            val maxAngleChange = PI.toFloat() / 8f
            val clampedDiff = angleDiff.coerceIn(-maxAngleChange, maxAngleChange)

            var newTheta = previousTheta + clampedDiff
             while (newTheta > PI) newTheta -= (2 * PI).toFloat()
            while (newTheta < -PI) newTheta += (2 * PI).toFloat()
            newTheta
        }

        // Map theta to perimeter
        // Ray direction
        val dx = cos(theta)
        val dy = sin(theta)

        // Find intersection with box bounds
        // Distance to vertical edges (x = +/- halfWidth)
        val tx = if (abs(dx) > 1e-6) halfWidth / abs(dx) else Float.POSITIVE_INFINITY
        // Distance to horizontal edges (y = +/- halfHeight)
        val ty = if (abs(dy) > 1e-6) halfHeight / abs(dy) else Float.POSITIVE_INFINITY

        val t = min(tx, ty)

        val localPerimeterPt = Offset(t * dx, t * dy)
        val globalPerimeterPt = localPerimeterPt.rotate(rotation) + center

        return Pair(globalPerimeterPt, theta)
    }

    /**
     * Returns positions for: left (rotate), right (aspect), top (scale), bottom (exit), center (move)
     */
    fun getHandlePositions(): RectangleHandlePositions {
        val handleOffset = 30f
        val halfWidth = width / 2f
        val halfHeight = height / 2f

        return RectangleHandlePositions(
            left = center + Offset(-halfWidth - handleOffset, 0f).rotate(rotation),
            right = center + Offset(halfWidth + handleOffset, 0f).rotate(rotation),
            top = center + Offset(0f, -halfHeight - handleOffset).rotate(rotation),
            bottom = center + Offset(0f, halfHeight + handleOffset).rotate(rotation),
            center = center
        )
    }

    /**
     * Update the center position
     */
    fun updateCenter(newCenter: Offset): RectangleState = copy(center = newCenter)

    /**
     * Update the rotation
     */
    fun updateRotation(newRotation: Float): RectangleState = copy(rotation = newRotation)

    /**
     * Calculate angle from center to a point
     */
    fun angleToPoint(point: Offset): Float {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return atan2(dy, dx)
    }

    /**
     * Update size dimensions
     */
    fun updateDimensions(newWidth: Float, newHeight: Float): RectangleState =
        copy(width = newWidth.coerceAtLeast(40f), height = newHeight.coerceAtLeast(40f))

    /**
     * Uniformly scale dimensions
     */
    fun scale(scaleFactor: Float): RectangleState {
        val newWidth = (width * scaleFactor).coerceIn(40f, 2000f)
        val newHeight = (height * scaleFactor).coerceIn(40f, 2000f)
        return copy(width = newWidth, height = newHeight)
    }
}

/**
 * Helper class to hold all handle positions
 */
data class RectangleHandlePositions(
    val left: Offset,    // Rotate handle
    val right: Offset,   // Resize W handle
    val top: Offset,     // Resize H handle
    val bottom: Offset,  // Exit handle
    val center: Offset   // Move handle
)

/**
 * Extension function to rotate an offset by a given angle
 */
private fun Offset.rotate(angleRadians: Float): Offset {
    val cos = cos(angleRadians)
    val sin = sin(angleRadians)
    return Offset(
        x * cos - y * sin,
        x * sin + y * cos
    )
}

/**
 * Get the distance of this offset from the origin
 */
private fun Offset.getDistance(): Float {
    return kotlin.math.sqrt(x * x + y * y)
}
