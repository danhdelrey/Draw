package com.example.draw.data.model.brush

import com.example.draw.data.model.util.generateId
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.air_brush
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

/**
 * Air brush for spray-paint effect with particle-based rendering.
 *
 * Creates a realistic spray-paint effect by:
 * - Distributing particles randomly in a circular area
 * - Varying particle opacity for natural look
 * - Density controls particle count
 * - Lower base opacity for subtle layering
 *
 * Properties:
 * - Density: Controls the number of particles per spray (0.0 to 1.0)
 *   - 0.0-0.3: Light spray, few particles
 *   - 0.4-0.6: Medium spray (default)
 *   - 0.7-1.0: Heavy spray, many particles
 * - Creates a softer, diffused stroke effect
 * - Opacity accumulates with overlapping strokes for build-up effect
 *
 * Recommended settings:
 * - Size: 20-50px for best effect
 * - Opacity: 0.2-0.4 for gradual build-up
 * - Density: 0.4-0.7 for natural look
 */
data class AirBrush(
    override val id: String = generateId(),
    override val size: Float = 35f,           // Larger default size for spray
    override val opacity: Float = 0.25f,       // Lower opacity for subtle effect
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.air_brush,
    override val properties: BrushProperties = BrushProperties(
        mapOf(BrushProperties.DENSITY to 0.5f)  // Medium density as default
    )
) : Brush {

    override val type: BrushType = BrushType.AIR

    /**
     * Convenience property for accessing density
     * Range: 0.0 (light spray) to 1.0 (heavy spray)
     */
    val density: Float
        get() = properties.getFloat(BrushProperties.DENSITY, 0.5f).coerceIn(0f, 1f)

    override fun updateSize(size: Float): Brush =
        copy(size = size)

    override fun updateOpacity(opacity: Float): Brush =
        copy(opacity = opacity)

    override fun updateColor(colorArgb: Long): Brush =
        copy(colorArgb = colorArgb)

    override fun updateProperties(properties: BrushProperties): Brush =
        copy(properties = properties)

    /**
     * Update density value (convenience method)
     */
    fun updateDensity(density: Float): AirBrush =
        copy(properties = properties.withProperty(BrushProperties.DENSITY, density))

    companion object {
        /**
         * Factory method for creating an air brush with default settings
         */
        fun default(): AirBrush = AirBrush()

        /**
         * Factory method for creating an air brush with specific density
         */
        fun withDensity(density: Float): AirBrush =
            AirBrush(properties = BrushProperties(mapOf(BrushProperties.DENSITY to density)))
    }
}




