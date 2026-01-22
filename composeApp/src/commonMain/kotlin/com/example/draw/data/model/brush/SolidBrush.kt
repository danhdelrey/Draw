package com.example.draw.data.model.brush

import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

data class SolidBrush(
    override val size: Float = 20f,
    override val opacity: Float = 1f,
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.solid_brush
) : Brush() {
    override fun updateSize(size: Float): Brush =
        copy(size = size)

    override fun updateOpacity(opacity: Float): Brush =
        copy(opacity = opacity)

    override fun updateColor(colorArgb: Long): Brush =
        copy(colorArgb = colorArgb)

}
