package com.example.draw.data.model.shape

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Represents the state of an ellipse for the Ellipse Drawing Mode.
 *
 * This is a temporary overlay state, not committed to drawing data
 * until user performs actual drawing action.
 *
 * @property center The center point of the ellipse in canvas coordinates
 * @property radiusX The horizontal radius (semi-major axis when radiusX > radiusY)
 * @property radiusY The vertical radius (semi-minor axis when radiusY < radiusX)
 * @property rotation The rotation angle in radians (clockwise)
 */
data class EllipseState(
    val center: Offset,
    val radiusX: Float,
    val radiusY: Float,
    val rotation: Float = 0f
) {
    /**
     * Creates a default ellipse (circle) centered at the given position
     */
    companion object {
        private const val DEFAULT_RADIUS = 150f

        fun createDefault(canvasWidth: Float, canvasHeight: Float): EllipseState {
            return EllipseState(
                center = Offset(canvasWidth / 2f, canvasHeight / 2f),
                radiusX = DEFAULT_RADIUS,
                radiusY = DEFAULT_RADIUS,
                rotation = 0f
            )
        }
    }

    /**
     * Check if this is a perfect circle
     */
    val isCircle: Boolean
        get() = radiusX == radiusY

    /**
     * Get a point on the ellipse perimeter at the given parametric angle (theta)
     * The angle is in radians, measured from the positive x-axis (before rotation)
     *
     * @param theta Parametric angle in radians
     * @return Point on the ellipse perimeter in canvas coordinates
     */
    fun pointOnPerimeter(theta: Float): Offset {
        // Point on unrotated ellipse
        val x = radiusX * cos(theta)
        val y = radiusY * sin(theta)

        // Apply rotation around center
        val rotatedX = x * cos(rotation) - y * sin(rotation)
        val rotatedY = x * sin(rotation) + y * cos(rotation)

        return Offset(center.x + rotatedX, center.y + rotatedY)
    }

    /**
     * Project a point onto the ellipse perimeter.
     * Finds the closest point on the ellipse to the given point.
     *
     * Uses radial projection approach: finds the angle from center to point,
     * then returns the point on the ellipse at that angle.
     *
     * This method ensures smooth, continuous stroke generation by:
     * 1. Using previous angle when point is too close to center
     * 2. Limiting maximum angle change per update to prevent jumps
     * 3. Handling angle wrap-around (from π to -π)
     *
     * @param point The point to project
     * @param previousTheta Optional previous angle for continuity when point is near center
     * @return Pair of (projected point on perimeter, theta angle used)
     */
    fun projectPointToPerimeterWithAngle(point: Offset, previousTheta: Float? = null): Pair<Offset, Float> {
        // Translate point relative to center
        val dx = point.x - center.x
        val dy = point.y - center.y

        // Reverse rotation to work in ellipse's local coordinate system
        val localX = dx * cos(-rotation) - dy * sin(-rotation)
        val localY = dx * sin(-rotation) + dy * cos(-rotation)

        // Calculate distance from center in local coordinates
        val distanceFromCenter = kotlin.math.sqrt(localX * localX + localY * localY)

        // Minimum distance threshold - if point is too close to center, use previous angle
        // Use a larger threshold for better stability
        val minDistance = kotlin.math.min(radiusX, radiusY) * 0.25f

        // Calculate raw theta from current position
        val rawTheta = atan2(localY / radiusY, localX / radiusX)

        val theta: Float = if (previousTheta == null) {
            // First point - use raw theta
            rawTheta
        } else if (distanceFromCenter < minDistance) {
            // Point is too close to center, use previous angle for continuity
            previousTheta
        } else {
            // Calculate the angle difference, handling wrap-around
            var angleDiff = rawTheta - previousTheta

            // Normalize angle difference to [-π, π]
            while (angleDiff > PI) angleDiff -= (2 * PI).toFloat()
            while (angleDiff < -PI) angleDiff += (2 * PI).toFloat()

            // Limit maximum angle change per update to prevent sudden jumps
            // Use smaller value for smoother strokes
            val maxAngleChange = PI.toFloat() / 8f  // 22.5 degrees max per update
            val clampedDiff = angleDiff.coerceIn(-maxAngleChange, maxAngleChange)

            // Apply clamped difference to previous angle
            var newTheta = previousTheta + clampedDiff

            // Normalize result to [-π, π]
            while (newTheta > PI) newTheta -= (2 * PI).toFloat()
            while (newTheta < -PI) newTheta += (2 * PI).toFloat()

            newTheta
        }

        // Return point on perimeter at this angle along with the angle
        return Pair(pointOnPerimeter(theta), theta)
    }

    /**
     * Project a point onto the ellipse perimeter (simple version without angle tracking).
     * For backward compatibility.
     *
     * @param point The point to project
     * @return The projected point on the ellipse perimeter
     */
    fun projectPointToPerimeter(point: Offset): Offset {
        return projectPointToPerimeterWithAngle(point, null).first
    }

    /**
     * Generate a list of points along the ellipse arc between two angles.
     * Useful for creating a smooth path along the ellipse.
     *
     * @param startTheta Starting angle in radians
     * @param endTheta Ending angle in radians
     * @param steps Number of intermediate points
     * @return List of points along the arc
     */
    fun generateArcPoints(startTheta: Float, endTheta: Float, steps: Int = 50): List<Offset> {
        val points = mutableListOf<Offset>()
        val thetaStep = (endTheta - startTheta) / steps

        for (i in 0..steps) {
            val theta = startTheta + i * thetaStep
            points.add(pointOnPerimeter(theta))
        }

        return points
    }

    /**
     * Get the angle from the ellipse center to a given point
     * Returns angle in radians, accounting for ellipse rotation
     */
    fun angleToPoint(point: Offset): Float {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return atan2(dy, dx)
    }

    /**
     * Calculate handle positions for the control overlay
     * Returns positions for: left (rotate), right (aspect), top (scale), bottom (exit), center (move)
     */
    fun getHandlePositions(): EllipseHandlePositions {
        // Handle distance from ellipse edge
        val handleOffset = 30f

        return EllipseHandlePositions(
            left = pointOnPerimeter(PI.toFloat()) - Offset(handleOffset, 0f).rotate(rotation),
            right = pointOnPerimeter(0f) + Offset(handleOffset, 0f).rotate(rotation),
            top = pointOnPerimeter(-PI.toFloat() / 2f) - Offset(0f, handleOffset).rotate(rotation),
            bottom = pointOnPerimeter(PI.toFloat() / 2f) + Offset(0f, handleOffset).rotate(rotation),
            center = center
        )
    }

    /**
     * Update the ellipse with a new center position
     */
    fun updateCenter(newCenter: Offset): EllipseState = copy(center = newCenter)

    /**
     * Update the rotation
     */
    fun updateRotation(newRotation: Float): EllipseState = copy(rotation = newRotation)

    /**
     * Update radii for aspect ratio control
     */
    fun updateRadii(newRadiusX: Float, newRadiusY: Float): EllipseState =
        copy(radiusX = newRadiusX.coerceAtLeast(20f), radiusY = newRadiusY.coerceAtLeast(20f))

    /**
     * Uniformly scale both radii
     */
    fun scale(scaleFactor: Float): EllipseState {
        val newRadiusX = (radiusX * scaleFactor).coerceIn(20f, 1000f)
        val newRadiusY = (radiusY * scaleFactor).coerceIn(20f, 1000f)
        return copy(radiusX = newRadiusX, radiusY = newRadiusY)
    }
}

/**
 * Helper class to hold all handle positions
 */
data class EllipseHandlePositions(
    val left: Offset,    // Rotate handle
    val right: Offset,   // Aspect control handle
    val top: Offset,     // Scale handle
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

