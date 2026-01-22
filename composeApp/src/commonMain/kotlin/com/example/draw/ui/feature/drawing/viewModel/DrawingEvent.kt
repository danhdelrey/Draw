package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath

sealed interface DrawingEvent{
    data class StartDrawing(val currentTouchPosition: Offset) : DrawingEvent
    data class UpdateDrawing(val currentTouchPosition: Offset) : DrawingEvent
    object EndDrawing : DrawingEvent
}