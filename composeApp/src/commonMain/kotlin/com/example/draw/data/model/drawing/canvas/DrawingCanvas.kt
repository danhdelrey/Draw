package com.example.draw.data.model.drawing.canvas

import com.example.draw.data.model.drawing.layer.Layer

data class DrawingCanvas(
    val width: Float,
    val height: Float,
    val layers: List<Layer>,
    val activeLayerId: String
)
