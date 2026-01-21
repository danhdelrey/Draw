package com.example.draw.data.model.brush

import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

data class BucketBrush(
    override val colorArgb: Long,
    override val imageResource: DrawableResource = Res.drawable.solid_brush
) : Brush() {

    override val size: Float = 0f
    override val opacity: Float = 1f
}
