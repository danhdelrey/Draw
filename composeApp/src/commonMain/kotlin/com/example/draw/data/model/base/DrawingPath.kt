package com.example.draw.data.model.base

import com.example.draw.data.model.brush.Brush

data class DrawingPath(
    val points: List<Point>,
    val brush: Brush
)

