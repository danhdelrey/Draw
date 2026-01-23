package com.example.draw.data.model.brush

import com.example.draw.data.model.util.generateId
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

/**
 * Air brush for spray-paint effect with particle-based rendering.
 *
 * Properties:
 * - Density: Controls the number of particles per spray
 * - Creates a softer, diffused stroke effect
 * - Opacity accumulates with overlapping strokes
 */
data class AirBrush(
    override val id: String = generateId(),
    override val size: Float = 30f,
    override val opacity: Float = 0.3f,
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.solid_brush,
    override val properties: BrushProperties = BrushProperties(
        mapOf(BrushProperties.DENSITY to 0.5f)
    )
) : Brush {

    override val type: BrushType = BrushType.AIR

    /**
     * Convenience property for accessing density
     */
    val density: Float
        get() = properties.getFloat(BrushProperties.DENSITY, 0.5f)

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




