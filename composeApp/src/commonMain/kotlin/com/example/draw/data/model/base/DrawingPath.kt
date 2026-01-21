package com.example.draw.data.model.base

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.brush.Brush

data class DrawingPath(
    val points: List<Offset>,
    val brush: Brush
)

