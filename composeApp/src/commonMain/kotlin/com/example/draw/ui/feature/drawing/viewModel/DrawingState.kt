package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush

data class DrawingState(
    val currentBrush: Brush = SolidBrush(),
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,
    val completedDrawingPaths: List<DrawingPath> = emptyList()
)
