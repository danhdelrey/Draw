package com.example.draw.data.model.layer

/**
 * Bitmap/Raster layer that stores drawing as pixel data.
 *
 * Properties:
 * - BitmapId: Reference to bitmap storage/cache
 * - Resolution-dependent: Quality depends on bitmap resolution
 * - Efficient for photo editing and raster effects
 *
 * Use cases:
 * - Photo editing
 * - Raster effects and filters
 * - Performance-optimized rendering of complex drawings
 */
data class BitmapLayer(
    override val id: String,
    override val name: String = "Bitmap Layer",
    override val isVisible: Boolean = true,
    override val isLocked: Boolean = false,
    override val opacity: Float = 1f,
    override val blendMode: BlendMode = BlendMode.NORMAL,
    override val metadata: LayerMetadata = LayerMetadata(type = LayerType.BITMAP),
    val bitmapId: String          // Reference to bitmap storage
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
     * Update the bitmap reference (convenience method)
     */
    fun updateBitmapId(bitmapId: String): BitmapLayer =
        copy(
            bitmapId = bitmapId,
            metadata = metadata.markModified()
        )

    companion object {
        /**
         * Factory method for creating a new bitmap layer with default settings
         */
        fun create(id: String, bitmapId: String, name: String = "Bitmap Layer"): BitmapLayer =
            BitmapLayer(id = id, bitmapId = bitmapId, name = name)
    }
}

