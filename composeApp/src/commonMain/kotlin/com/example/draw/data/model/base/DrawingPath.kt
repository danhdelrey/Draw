package com.example.draw.data.model.base

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.util.currentTimeMillis
import com.example.draw.data.model.util.generateId

/**
 * Represents a single drawing path/stroke in the vector layer.
 *
 * Design principles:
 * - Immutable: All properties are val
 * - Self-contained: Contains all information needed to render the stroke
 * - Extensible: Can add metadata for advanced features
 *
 * Properties:
 * - id: Unique identifier for the path
 * - points: Ordered list of touch points that form the stroke
 * - brush: Brush configuration used for this stroke
 * - metadata: Additional properties for advanced features
 */
data class DrawingPath(
    val id: String = generateId(),
    val points: List<Offset>,
    val brush: Brush,
    val metadata: PathMetadata = PathMetadata()
) {
    /**
     * Check if this path is valid (has at least 2 points)
     */
    val isValid: Boolean
        get() = points.size >= 2

    /**
     * Get the bounding box of this path (for optimization)
     */
    val bounds: PathBounds?
        get() = if (points.isEmpty()) null else {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }
            PathBounds(minX, minY, maxX, maxY)
        }

    /**
     * Add a new point to this path (convenience method)
     */
    fun addPoint(point: Offset): DrawingPath =
        copy(points = points + point)

    /**
     * Update the brush for this path
     */
    fun updateBrush(brush: Brush): DrawingPath =
        copy(brush = brush)

    companion object {
        /**
         * Factory method for creating a new path with initial point
         */
        fun create(initialPoint: Offset, brush: Brush): DrawingPath =
            DrawingPath(points = listOf(initialPoint), brush = brush)

        /**
         * Factory method for creating an empty path
         */
        fun empty(brush: Brush): DrawingPath =
            DrawingPath(points = emptyList(), brush = brush)
    }
}

/**
 * Metadata for drawing paths.
 * Can be extended with properties like:
 * - Pressure information for stylus support
 * - Timestamp for animation
 * - Custom properties for effects
 */
data class PathMetadata(
    val createdAt: Long = currentTimeMillis(),
    val pressure: List<Float>? = null,  // Pressure for each point (if available)
    val properties: Map<String, Any> = emptyMap()
) {
    fun withProperty(key: String, value: Any): PathMetadata =
        copy(properties = properties + (key to value))
}

/**
 * Bounding box for path optimization and culling
 */
data class PathBounds(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
    val centerX: Float get() = (minX + maxX) / 2f
    val centerY: Float get() = (minY + maxY) / 2f

    /**
     * Check if a point is within these bounds (with optional padding)
     */
    fun contains(x: Float, y: Float, padding: Float = 0f): Boolean =
        x >= minX - padding && x <= maxX + padding &&
        y >= minY - padding && y <= maxY + padding

    /**
     * Check if this bounds intersects with another
     */
    fun intersects(other: PathBounds): Boolean =
        !(maxX < other.minX || minX > other.maxX ||
          maxY < other.minY || minY > other.maxY)
}


