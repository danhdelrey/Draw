package com.example.draw.data.model.brush

data class EraserBrush(
    override val size: Float,
    override val opacity: Float = 1f
) : Brush() {

    override val colorArgb: Long = 0x00000000
}
