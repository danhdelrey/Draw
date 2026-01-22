package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush

data class DrawingState(
    var currentBrush: Brush = SolidBrush(),
    var currentTouchPosition: Offset? = null,
    var currentDrawingPath: DrawingPath? = null,
    var completedDrawingPaths: List<DrawingPath> = emptyList()
)
