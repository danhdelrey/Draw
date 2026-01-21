package com.example.draw.data.model.layer

data class BitmapLayer(
    override val id: String,
    override val isVisible: Boolean = true,
    override val isLocked: Boolean = false,
    override val opacity: Float = 1f,
    val bitmapId: String          // reference tới bitmap storage
) : Layer()