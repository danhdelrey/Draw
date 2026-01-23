package com.example.draw.data.model.brush

import com.example.draw.data.model.util.generateId
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

/**
 * Solid brush for standard drawing with uniform stroke.
 *
 * Properties:
 * - Consistent opacity throughout the stroke
 * - No special effects or patterns
 * - Default brush for most drawing operations
 */
data class SolidBrush(
    override val id: String = generateId(),
    override val size: Float = 20f,
    override val opacity: Float = 1f,
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.solid_brush,
    override val properties: BrushProperties = BrushProperties()
) : Brush {

    override val type: BrushType = BrushType.SOLID

    override fun updateSize(size: Float): Brush =
        copy(size = size)

    override fun updateOpacity(opacity: Float): Brush =
        copy(opacity = opacity)

    override fun updateColor(colorArgb: Long): Brush =
        copy(colorArgb = colorArgb)

    override fun updateProperties(properties: BrushProperties): Brush =
        copy(properties = properties)

    companion object {
        /**
         * Factory method for creating a solid brush with default settings
         */
        fun default(): SolidBrush = SolidBrush()

        /**
         * Factory method for creating a solid brush with specific color
         */
        fun withColor(colorArgb: Long): SolidBrush =
            SolidBrush(colorArgb = colorArgb)
    }
}

