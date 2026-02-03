package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.model.shape.EllipseState

sealed interface DrawingEvent{
    //load
    data class LoadInitialState(val state: DrawingState) : DrawingEvent
    //Draw
    data class StartDrawing(val currentTouchPosition: Offset) : DrawingEvent
    data class UpdateDrawing(val currentTouchPosition: Offset) : DrawingEvent
    object EndDrawing : DrawingEvent
    object CancelDrawing : DrawingEvent
    data class SaveDrawing(val imageBitmap: ImageBitmap) : DrawingEvent

    //Brush
    data class ChangeBrush(val brush: Brush) : DrawingEvent

    //Layer
    data class SelectLayer(val layer: Layer) : DrawingEvent
    data class DeleteLayer(val layer: Layer) : DrawingEvent
    data class ToggleLayerVisibility(val layer: Layer) : DrawingEvent
    data class InvertLayer(val layer: Layer) : DrawingEvent // Add InvertLayer event
    data class ReorderLayer(val fromIndex: Int, val toIndex: Int) : DrawingEvent
    object AddLayer : DrawingEvent

    //Undo/Redo
    object Undo : DrawingEvent
    object Redo : DrawingEvent

    //save project
    data class SaveDrawingProject(val state: DrawingState) : DrawingEvent

    // Ellipse Drawing Mode
    object EnterEllipseMode : DrawingEvent
    object ExitEllipseMode : DrawingEvent
    data class UpdateEllipseCenter(val center: Offset) : DrawingEvent
    data class UpdateEllipseRadii(val radiusX: Float, val radiusY: Float) : DrawingEvent
    data class UpdateEllipseRotation(val rotation: Float) : DrawingEvent
    data class UpdateEllipseScale(val scaleFactor: Float) : DrawingEvent
    data class UpdateEllipseState(val ellipseState: EllipseState) : DrawingEvent
}