package com.example.draw.data.model.brush

import com.example.draw.data.model.util.generateId
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

/**
 * Bucket/Fill brush for flood-fill operations.
 *
 * Properties:
 * - Size is fixed at 0 (not applicable for fill operations)
 * - Opacity is fixed at 1.0 (fills are solid)
 * - Only color can be changed
 * - Can be extended with tolerance property for fill algorithm
 */
data class BucketBrush(
    override val id: String = generateId(),
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.solid_brush,
    override val properties: BrushProperties = BrushProperties(
        mapOf("tolerance" to 0.1f)  // Color matching tolerance for fill
    )
) : Brush {

    override val type: BrushType = BrushType.BUCKET
    override val size: Float = 0f      // Not applicable for fill
    override val opacity: Float = 1f   // Fills are always solid

    /**
     * Convenience property for accessing fill tolerance
     */
    val tolerance: Float
        get() = properties.getFloat("tolerance", 0.1f)

    override fun updateSize(size: Float): Brush =
        this  // Size not applicable for bucket

    override fun updateOpacity(opacity: Float): Brush =
        this  // Opacity fixed for bucket

    override fun updateColor(colorArgb: Long): Brush =
        copy(colorArgb = colorArgb)

    override fun updateProperties(properties: BrushProperties): Brush =
        copy(properties = properties)

    /**
     * Update fill tolerance (convenience method)
     */
    fun updateTolerance(tolerance: Float): BucketBrush =
        copy(properties = properties.withProperty("tolerance", tolerance))

    companion object {
        /**
         * Factory method for creating a bucket brush with default settings
         */
        fun default(): BucketBrush = BucketBrush()

        /**
         * Factory method for creating a bucket brush with specific color
         */
        fun withColor(colorArgb: Long): BucketBrush =
            BucketBrush(colorArgb = colorArgb)

        /**
         * Factory method for creating a bucket brush with specific tolerance
         */
        fun withTolerance(tolerance: Float): BucketBrush =
            BucketBrush(properties = BrushProperties(mapOf("tolerance" to tolerance)))
    }
}




