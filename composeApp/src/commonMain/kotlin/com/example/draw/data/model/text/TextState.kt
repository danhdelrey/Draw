package com.example.draw.data.model.text

import androidx.compose.ui.geometry.Offset

data class TextState(
    val center: Offset,
    val radiusX: Float,
    val radiusY: Float,
    val rotation: Float = 0f
)
