package com.example.draw.data.model.brush

import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.eraser
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

data class EraserBrush(
    override val size: Float = 20f,
    override val opacity: Float = 1f,
    override val imageResource: DrawableResource = Res.drawable.eraser
) : Brush() {

    override val colorArgb: Long = 0x00000000
}
