package com.example.draw.data.model.canvas

import com.example.draw.data.model.layer.Layer

data class DrawingCanvas(
    val width: Float,
    val height: Float,
    val layers: List<Layer>,
    val activeLayerId: String
)
