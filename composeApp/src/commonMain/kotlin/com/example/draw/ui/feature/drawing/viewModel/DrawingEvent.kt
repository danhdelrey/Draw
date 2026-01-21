package com.example.draw.ui.feature.drawing.viewModel

import com.example.draw.data.model.base.DrawingPath

sealed interface DrawingEvent{
    data class StartDrawing(val drawingPath: DrawingPath) : DrawingEvent
}