package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.canvasOverlay.RectBoundOverlay
import com.example.draw.ui.feature.drawing.component.EllipseOverlay
import com.example.draw.ui.feature.drawing.component.RectangleOverlay
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

@Composable
fun CanvasOverlays(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    renderScale: Float,
    inputScale: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    layerTransformOrigin: TransformOrigin
) {
    val activeTransformLayer = state.layers
        .firstOrNull { it.id == (state.transformLayerId ?: state.canvas.activeLayerId) }
        as? VectorLayer

    // --- TRANSFORM LAYER MODE INDICATOR ---
    RectBoundOverlay(
        shouldShowRectOverlay = state.isInLayerTransformationMode,
        drawingPaths = activeTransformLayer?.paths ?: emptyList(),
        sX = state.layerTransformState.scale,
        sY = state.layerTransformState.scale,
        rZ = state.layerTransformState.rotation,
        transX = state.layerTransformState.translation.x,
        transY = state.layerTransformState.translation.y,
        transform = layerTransformOrigin,
        canvasHeight = canvasHeight,
        canvasWidth = canvasWidth,
        layerTransformPivot = state.layerTransformPivot,
        onUpdateTransformPivot = { newPivot ->
            viewModel.onEvent(DrawingEvent.UpdateLayerTransformPivot(newPivot))
        },
    )

    // --- ELLIPSE OVERLAY (Inside transformed box for alignment) ---
    val ellipseMode = state.ellipseMode
    if (ellipseMode != null) {
        EllipseOverlay(
            ellipseState = ellipseMode,
            renderScale = renderScale,
            inputScale = inputScale,
            // Inside the canvas box, offset is 0
            canvasOffsetX = 0f,
            canvasOffsetY = 0f,
            onUpdateEllipse = { newEllipse ->
                viewModel.onEvent(DrawingEvent.UpdateEllipseState(newEllipse))
            },
            onExitEllipseMode = {
                viewModel.onEvent(DrawingEvent.ExitEllipseMode)
            },
            onStartDrawing = { offset ->
                viewModel.onEvent(DrawingEvent.StartDrawing(offset))
            },
            onUpdateDrawing = { offset ->
                viewModel.onEvent(DrawingEvent.UpdateDrawing(offset))
            },
            onEndDrawing = {
                viewModel.onEvent(DrawingEvent.EndDrawing)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // --- RECTANGLE OVERLAY (Inside transformed box for alignment) ---
    val rectangleMode = state.rectangleMode
    if (rectangleMode != null) {
        RectangleOverlay(
            rectangleState = rectangleMode,
            renderScale = renderScale,
            inputScale = inputScale,
            // Inside the canvas box, offset is 0
            canvasOffsetX = 0f,
            canvasOffsetY = 0f,
            onUpdateRectangle = { newRect ->
                viewModel.onEvent(DrawingEvent.UpdateRectangleState(newRect))
            },
            onExitRectangleMode = {
                viewModel.onEvent(DrawingEvent.ExitRectangleMode)
            },
            onStartDrawing = { offset ->
                viewModel.onEvent(DrawingEvent.StartDrawing(offset))
            },
            onUpdateDrawing = { offset ->
                viewModel.onEvent(DrawingEvent.UpdateDrawing(offset))
            },
            onEndDrawing = {
                viewModel.onEvent(DrawingEvent.EndDrawing)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

