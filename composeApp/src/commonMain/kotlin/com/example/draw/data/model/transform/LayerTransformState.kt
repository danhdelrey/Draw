package com.example.draw.data.model.transform

import androidx.compose.ui.geometry.Offset

/**
 * State for layer transformation (scale, rotation, translation)
 */
data class LayerTransformState(
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val translation: Offset = Offset.Zero
)
