package com.example.draw.data.model.brush

import org.jetbrains.compose.resources.DrawableResource

sealed class Brush {

    abstract val size: Float        // stroke width
    abstract val opacity: Float     // 0f..1f
    abstract val colorArgb: Long    // 0xAARRGGBB
    abstract val imageResource: DrawableResource

}
