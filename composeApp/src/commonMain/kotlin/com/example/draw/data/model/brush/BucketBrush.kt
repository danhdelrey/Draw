package com.example.draw.data.model.brush

data class BucketBrush(
    override val colorArgb: Long
) : Brush() {

    override val size: Float = 0f
    override val opacity: Float = 1f
}
