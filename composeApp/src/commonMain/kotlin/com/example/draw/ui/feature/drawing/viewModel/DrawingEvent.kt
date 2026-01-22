package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush

sealed interface DrawingEvent{
    //Draw
    data class StartDrawing(val currentTouchPosition: Offset) : DrawingEvent
    data class UpdateDrawing(val currentTouchPosition: Offset) : DrawingEvent
    object EndDrawing : DrawingEvent

    //Brush
    data class ChangeBrush(val brush: Brush) : DrawingEvent
}