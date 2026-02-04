package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

@Composable
fun CanvasLayerList(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    renderScale: Float,
    inputScale: Float,
    layerTransformOrigin: TransformOrigin
) {
    state.layers.forEach { layer ->
        if (layer.isVisible && layer is VectorLayer) {
            val isActiveLayer = layer.id == state.canvas.activeLayerId
            val isTransformedLayer = layer.id == (state.transformLayerId ?: state.canvas.activeLayerId)
            val pathBeingDrawn = if (isActiveLayer) state.currentDrawingPath else null
            val touchPos = if (isActiveLayer) state.currentTouchPosition else null

            // --- LAYER DISPLAY ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = layer.opacity
                        compositingStrategy = CompositingStrategy.Offscreen

                        if (isTransformedLayer && state.isInLayerTransformationMode) {
                            scaleX = state.layerTransformState.scale
                            scaleY = state.layerTransformState.scale
                            rotationZ = state.layerTransformState.rotation
                            translationX =
                                state.layerTransformState.translation.x * renderScale
                            translationY =
                                state.layerTransformState.translation.y * renderScale
                            transformOrigin = layerTransformOrigin
                        }
                    }
            ) {
                DrawingCanvas(
                    paths = layer.paths,
                    currentPath = pathBeingDrawn,
                    isEraserMode = state.currentBrush is EraserBrush,
                    currentTouchPosition = touchPos,
                    brushSize = state.currentBrush.size,
                    renderScale = renderScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // --- LAYER INPUT (only for normal mode, not ellipse/rectangle mode) ---
            if (isActiveLayer && state.ellipseMode == null && state.rectangleMode == null && !state.isInLayerTransformationMode) {
                // Normal drawing mode - Inputs are localized due to wrapper graphicsLayer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawingInput(
                            onDragStart = { offset -> viewModel.onEvent(DrawingEvent.StartDrawing(offset * inputScale)) },
                            onDrag = { offset -> viewModel.onEvent(DrawingEvent.UpdateDrawing(offset * inputScale)) },
                            onDragEnd = { viewModel.onEvent(DrawingEvent.EndDrawing) },
                            onDragCancel = { viewModel.onEvent(DrawingEvent.CancelDrawing) }
                        )
                )
            }
        }
    }
}

