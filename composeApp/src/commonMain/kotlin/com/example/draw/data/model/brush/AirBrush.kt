package com.example.draw.data.model.brush

data class AirBrush(
    override val size: Float,
    override val opacity: Float,
    override val colorArgb: Long,
    val density: Float            // mật độ hạt
) : Brush()
