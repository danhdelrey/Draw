package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.layer.Layer

sealed interface DrawingEvent{
    //Draw
    data class StartDrawing(val currentTouchPosition: Offset) : DrawingEvent
    data class UpdateDrawing(val currentTouchPosition: Offset) : DrawingEvent
    object EndDrawing : DrawingEvent
    data class SaveDrawing(val imageBitmap: ImageBitmap) : DrawingEvent

    //Brush
    data class ChangeBrush(val brush: Brush) : DrawingEvent

    //Layer
    data class SelectLayer(val layer: Layer) : DrawingEvent
    data class DeleteLayer(val layer: Layer) : DrawingEvent
    data class ToggleLayerVisibility(val layer: Layer) : DrawingEvent
    object AddLayer : DrawingEvent
}