package com.example.draw.data.model.layer

import com.example.draw.data.model.base.DrawingPath

/**
 * Vector layer that stores drawing as a collection of paths.
 *
 * Properties:
 * - Paths: List of vector drawing paths with brush information
 * - Resolution-independent: Can be scaled without quality loss
 * - Efficient for line-based drawings
 *
 * Use cases:
 * - Sketch and line art
 * - Undo/redo individual strokes
 * - Non-destructive editing
 */
data class VectorLayer(
    override val id: String,
    override val name: String = "Vector Layer",
    override val isVisible: Boolean = true,
    override val isLocked: Boolean = false,
    override val opacity: Float = 1f,
    override val blendMode: BlendMode = BlendMode.NORMAL,
    override val metadata: LayerMetadata = LayerMetadata(type = LayerType.VECTOR),
    val paths: List<DrawingPath> = emptyList()
) : Layer {

    override fun updateVisibility(isVisible: Boolean): Layer =
        copy(isVisible = isVisible, metadata = metadata.markModified())

    override fun updateLocked(isLocked: Boolean): Layer =
        copy(isLocked = isLocked, metadata = metadata.markModified())

    override fun updateOpacity(opacity: Float): Layer =
        copy(opacity = opacity, metadata = metadata.markModified())

    override fun updateName(name: String): Layer =
        copy(name = name, metadata = metadata.markModified())

    override fun updateBlendMode(blendMode: BlendMode): Layer =
        copy(blendMode = blendMode, metadata = metadata.markModified())

    override fun updateMetadata(metadata: LayerMetadata): Layer =
        copy(metadata = metadata)

    /**
     * Add a new path to this layer (convenience method)
     */
    fun addPath(path: DrawingPath): VectorLayer =
        copy(
            paths = paths + path,
            metadata = metadata.markModified()
        )

    /**
     * Remove a specific path from this layer (convenience method)
     */
    fun removePath(path: DrawingPath): VectorLayer =
        copy(
            paths = paths - path,
            metadata = metadata.markModified()
        )

    /**
     * Replace all paths in this layer (convenience method)
     */
    fun updatePaths(paths: List<DrawingPath>): VectorLayer =
        copy(
            paths = paths,
            metadata = metadata.markModified()
        )

    /**
     * Clear all paths from this layer (convenience method)
     */
    fun clearPaths(): VectorLayer =
        copy(
            paths = emptyList(),
            metadata = metadata.markModified()
        )

    companion object {
        /**
         * Factory method for creating a new vector layer with default settings
         */
        fun create(id: String, name: String = "Vector Layer"): VectorLayer =
            VectorLayer(id = id, name = name)
    }
}

