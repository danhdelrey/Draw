package com.example.draw.data.model.canvas

import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.util.currentTimeMillis
import com.example.draw.data.model.util.generateId

/**
 * Represents the complete drawing canvas with all layers and configuration.
 *
 * Design principles:
 * - Immutable: All updates create new instances
 * - Complete state: Contains all information needed to render and save
 * - Extensible: Can add canvas-level properties without breaking existing code
 *
 * Properties:
 * - id: Unique identifier for this canvas/document
 * - width/height: Canvas dimensions in pixels
 * - layers: Ordered list of layers (bottom to top)
 * - activeLayerId: Currently selected layer for editing
 * - metadata: Additional canvas properties
 */
data class DrawingCanvas(
    val id: String = generateId(),
    val width: Float,
    val height: Float,
    val layers: List<Layer>,
    val activeLayerId: String,
    val metadata: CanvasMetadata = CanvasMetadata()
) {
    /**
     * Get the active layer (convenience property)
     */
    val activeLayer: Layer?
        get() = layers.find { it.id == activeLayerId }

    /**
     * Get only visible layers (convenience property)
     */
    val visibleLayers: List<Layer>
        get() = layers.filter { it.isVisible }

    /**
     * Update layers list
     */
    fun updateLayers(layers: List<Layer>): DrawingCanvas =
        copy(
            layers = layers,
            metadata = metadata.markModified()
        )

    /**
     * Update a specific layer
     */
    fun updateLayer(layerId: String, update: (Layer) -> Layer): DrawingCanvas {
        val updatedLayers = layers.map { layer ->
            if (layer.id == layerId) update(layer) else layer
        }
        return copy(
            layers = updatedLayers,
            metadata = metadata.markModified()
        )
    }

    /**
     * Add a new layer
     */
    fun addLayer(layer: Layer, position: Int? = null): DrawingCanvas {
        val updatedLayers = if (position != null) {
            layers.toMutableList().apply { add(position, layer) }
        } else {
            layers + layer
        }
        return copy(
            layers = updatedLayers,
            metadata = metadata.markModified()
        )
    }

    /**
     * Remove a layer by ID
     */
    fun removeLayer(layerId: String): DrawingCanvas {
        val index = layers.indexOfFirst { it.id == layerId }
        val updatedLayers = layers.filter { it.id != layerId }

        val newActiveId = if (activeLayerId == layerId) {
            if (updatedLayers.isEmpty()) {
                ""
            } else if (index != -1) {
                // Select layer above (which takes the deleted layer's index)
                // or layer below if strictly top layer was deleted
                val newIndex = index.coerceAtMost(updatedLayers.lastIndex)
                updatedLayers[newIndex].id
            } else {
                updatedLayers.first().id
            }
        } else {
            activeLayerId
        }

        return copy(
            layers = updatedLayers,
            activeLayerId = newActiveId,
            metadata = metadata.markModified()
        )
    }

    /**
     * Change active layer
     */
    fun setActiveLayer(layerId: String): DrawingCanvas =
        copy(activeLayerId = layerId)

    /**
     * Reorder layers
     */
    fun moveLayer(fromIndex: Int, toIndex: Int): DrawingCanvas {
        val updatedLayers = layers.toMutableList().apply {
            val layer = removeAt(fromIndex)
            add(toIndex, layer)
        }
        return copy(
            layers = updatedLayers,
            metadata = metadata.markModified()
        )
    }

    companion object {
        /**
         * Factory method for creating a new canvas with default settings
         */
        fun create(
            width: Float,
            height: Float,
            initialLayer: Layer
        ): DrawingCanvas = DrawingCanvas(
            width = width,
            height = height,
            layers = listOf(initialLayer),
            activeLayerId = initialLayer.id
        )
    }
}

/**
 * Metadata for canvas/document properties.
 * Can be extended with:
 * - Document title
 * - Author information
 * - Save history
 * - Custom properties
 */
data class CanvasMetadata(
    val title: String = "Untitled",
    val createdAt: Long = currentTimeMillis(),
    val modifiedAt: Long = currentTimeMillis(),
    val backgroundColor: Long = 0xFFFFFFFF,  // Default white background
    val properties: Map<String, Any> = emptyMap()
) {
    /**
     * Mark as modified (updates timestamp)
     */
    fun markModified(): CanvasMetadata =
        copy(modifiedAt = currentTimeMillis())

    /**
     * Update title
     */
    fun updateTitle(title: String): CanvasMetadata =
        copy(title = title, modifiedAt = currentTimeMillis())

    /**
     * Update background color
     */
    fun updateBackgroundColor(color: Long): CanvasMetadata =
        copy(backgroundColor = color, modifiedAt = currentTimeMillis())

    /**
     * Add or update a custom property
     */
    fun withProperty(key: String, value: Any): CanvasMetadata =
        copy(
            properties = properties + (key to value),
            modifiedAt = currentTimeMillis()
        )
}

