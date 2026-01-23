package com.example.draw.data.model.layer

import com.example.draw.data.model.util.currentTimeMillis

/**
 * Base sealed interface for all layer types in the drawing application.
 *
 * Design principles:
 * - Immutable: All properties are val, updates create new instances
 * - Type-safe: Each layer type can have specific data
 * - Extensible: Easy to add new layer types (adjustment layers, text layers, etc.)
 */
sealed interface Layer {
    val id: String              // Unique identifier
    val name: String            // Display name for UI
    val isVisible: Boolean      // Visibility toggle
    val isLocked: Boolean       // Lock editing
    val opacity: Float          // Layer opacity (0f..1f)
    val blendMode: BlendMode    // How layer blends with layers below
    val metadata: LayerMetadata // Additional layer metadata

    /**
     * Creates a new instance with updated visibility.
     */
    fun updateVisibility(isVisible: Boolean): Layer

    /**
     * Creates a new instance with updated lock state.
     */
    fun updateLocked(isLocked: Boolean): Layer

    /**
     * Creates a new instance with updated opacity.
     */
    fun updateOpacity(opacity: Float): Layer

    /**
     * Creates a new instance with updated name.
     */
    fun updateName(name: String): Layer

    /**
     * Creates a new instance with updated blend mode.
     */
    fun updateBlendMode(blendMode: BlendMode): Layer

    /**
     * Creates a new instance with updated metadata.
     */
    fun updateMetadata(metadata: LayerMetadata): Layer
}

/**
 * Enumeration of blend modes for layer compositing.
 * Can be extended with more blend modes as needed.
 */
enum class BlendMode {
    NORMAL,
    MULTIPLY,
    SCREEN,
    OVERLAY,
    // Easy to extend: DARKEN, LIGHTEN, COLOR_DODGE, etc.
}

/**
 * Layer type classification for type checking and UI organization.
 */
enum class LayerType {
    VECTOR,
    BITMAP,
    // Easy to extend: TEXT, ADJUSTMENT, GROUP, SHAPE, etc.
}

/**
 * Container for layer metadata and properties.
 * Uses a map-based approach for maximum flexibility.
 */
data class LayerMetadata(
    val type: LayerType,
    val createdAt: Long = currentTimeMillis(),
    val modifiedAt: Long = currentTimeMillis(),
    val properties: Map<String, Any> = emptyMap()
) {
    /**
     * Type-safe getters for metadata properties
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
     * Create a new instance with an updated property
     */
    fun withProperty(key: String, value: Any): LayerMetadata =
        copy(
            properties = properties + (key to value),
            modifiedAt = currentTimeMillis()
        )

    /**
     * Create a new instance marking as modified
     */
    fun markModified(): LayerMetadata =
        copy(modifiedAt = currentTimeMillis())
}




