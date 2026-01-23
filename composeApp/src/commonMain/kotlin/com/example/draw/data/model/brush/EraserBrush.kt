package com.example.draw.data.model.brush

import com.example.draw.data.model.util.generateId
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.eraser
import org.jetbrains.compose.resources.DrawableResource

/**
 * Eraser brush for removing drawn content.
 *
 * Properties:
 * - Color is always transparent (0x00000000)
 * - Size and opacity are configurable
 * - Color cannot be changed (updateColor returns same instance)
 */
data class EraserBrush(
    override val id: String = generateId(),
    override val size: Float = 20f,
    override val opacity: Float = 1f,
    override val imageResource: DrawableResource = Res.drawable.eraser,
    override val properties: BrushProperties = BrushProperties()
) : Brush {

    override val type: BrushType = BrushType.ERASER
    override val colorArgb: Long = 0x00000000  // Always transparent

    override fun updateSize(size: Float): Brush =
        copy(size = size)

    override fun updateOpacity(opacity: Float): Brush =
        copy(opacity = opacity)

    override fun updateColor(colorArgb: Long): Brush =
        this  // Eraser color cannot be changed

    override fun updateProperties(properties: BrushProperties): Brush =
        copy(properties = properties)

    companion object {
        /**
         * Factory method for creating an eraser with default settings
         */
        fun default(): EraserBrush = EraserBrush()

        /**
         * Factory method for creating a large eraser
         */
        fun large(): EraserBrush = EraserBrush(size = 50f)

        /**
         * Factory method for creating a small eraser
         */
        fun small(): EraserBrush = EraserBrush(size = 10f)
    }
}

