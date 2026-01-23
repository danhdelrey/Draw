package com.example.draw.data.model.brush

import org.jetbrains.compose.resources.DrawableResource

/**
 * Base sealed interface for all brush types in the drawing application.
 *
 * Design principles:
 * - Immutable: All properties are val, updates create new instances
 * - Type-safe: Each brush type can have specific properties
 * - Extensible: Easy to add new brush types without modifying existing code
 */
sealed interface Brush {
    val id: String                  // Unique identifier for brush instance
    val type: BrushType             // Type classification
    val size: Float                 // Stroke width (in pixels)
    val opacity: Float              // Transparency level (0f..1f)
    val colorArgb: Long             // Color in ARGB format (0xAARRGGBB)
    val imageResource: DrawableResource  // Icon for UI representation
    val properties: BrushProperties // Additional configurable properties

    /**
     * Creates a new instance with updated size.
     * @param size New stroke width
     * @return New brush instance with updated size
     */
    fun updateSize(size: Float): Brush

    /**
     * Creates a new instance with updated opacity.
     * @param opacity New transparency level (0f..1f)
     * @return New brush instance with updated opacity
     */
    fun updateOpacity(opacity: Float): Brush

    /**
     * Creates a new instance with updated color.
     * @param colorArgb New color in ARGB format
     * @return New brush instance with updated color
     */
    fun updateColor(colorArgb: Long): Brush

    /**
     * Creates a new instance with updated custom properties.
     * @param properties New brush properties
     * @return New brush instance with updated properties
     */
    fun updateProperties(properties: BrushProperties): Brush
}

/**
 * Enumeration of all available brush types.
 * Makes it easy to add new brush types and handle them in when expressions.
 */
enum class BrushType {
    SOLID,
    AIR,
    ERASER,
    BUCKET,
    // Easy to extend: WATERCOLOR, PENCIL, MARKER, etc.
}

/**
 * Container for brush-specific configurable properties.
 * Uses a map-based approach for maximum flexibility and extensibility.
 *
 * Common property keys:
 * - "density": Float - For air brush particle density
 * - "hardness": Float - Edge hardness (0f = soft, 1f = hard)
 * - "spacing": Float - Distance between stamps
 * - "flow": Float - Paint flow rate
 * - "smoothing": Float - Stroke smoothing level
 */
data class BrushProperties(
    val properties: Map<String, Any> = emptyMap()
) {
    /**
     * Type-safe getters for common properties
     */
    fun getFloat(key: String, default: Float = 0f): Float =
        (properties[key] as? Float) ?: default

    fun getInt(key: String, default: Int = 0): Int =
        (properties[key] as? Int) ?: default

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        (properties[key] as? Boolean) ?: default

    fun getString(key: String, default: String = ""): String =
        (properties[key] as? String) ?: default

    /**
     * Create a new instance with an added/updated property
     */
    fun withProperty(key: String, value: Any): BrushProperties =
        copy(properties = properties + (key to value))

    /**
     * Create a new instance without a property
     */
    fun withoutProperty(key: String): BrushProperties =
        copy(properties = properties - key)

    companion object {
        // Property key constants for type safety
        const val DENSITY = "density"
        const val HARDNESS = "hardness"
        const val SPACING = "spacing"
        const val FLOW = "flow"
        const val SMOOTHING = "smoothing"
    }
}
