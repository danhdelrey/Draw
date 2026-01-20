package com.example.draw.data.model.brush

data class SolidBrush(
    override val size: Float,
    override val opacity: Float,
    override val colorArgb: Long
) : Brush()
