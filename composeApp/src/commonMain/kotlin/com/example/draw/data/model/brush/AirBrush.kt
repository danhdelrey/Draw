package com.example.draw.data.model.brush

import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource

data class AirBrush(
    override val size: Float,
    override val opacity: Float,
    override val colorArgb: Long,
    override val imageResource: DrawableResource = Res.drawable.solid_brush,
    val density: Float            // mật độ hạt
) : Brush() {
    override fun updateSize(size: Float): Brush {
        TODO("Not yet implemented")
    }

    override fun updateOpacity(opacity: Float): Brush {
        TODO("Not yet implemented")
    }

    override fun updateColor(colorArgb: Long): Brush {
        TODO("Not yet implemented")
    }
}
