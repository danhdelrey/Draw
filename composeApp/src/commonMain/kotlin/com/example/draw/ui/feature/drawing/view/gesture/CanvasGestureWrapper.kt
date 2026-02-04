package com.example.draw.ui.feature.drawing.view.gesture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.example.draw.data.model.transform.LayerTransformState
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

@Composable
fun CanvasGestureWrapper(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    renderScale: Float,
    layoutOffsetX: Float,
    layoutOffsetY: Float,
    zoom: Float,
    angle: Float,
    translation: Offset,
    onViewTransformChange: (Float, Float, Offset) -> Unit,
    content: @Composable () -> Unit
) {
    val currentRenderScaleState = rememberUpdatedState(renderScale)
    val currentStateState = rememberUpdatedState(state)
    val currentViewModelState = rememberUpdatedState(viewModel)
    val layoutXState = rememberUpdatedState(layoutOffsetX)
    val layoutYState = rememberUpdatedState(layoutOffsetY)

    val currentZoomState = rememberUpdatedState(zoom)
    val currentAngleState = rememberUpdatedState(angle)
    val currentTranslationState = rememberUpdatedState(translation)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Local state trackers to handle high-frequency events without waiting for recomposition
                var activeZoom = 1f
                var activeAngle = 0f
                var activeTranslation = Offset.Zero

                detectCanvasGestures(
                    shouldIntercept = { pointerCount ->
                        val currentDrawingState = currentStateState.value
                        val isLayerTransform = currentDrawingState.isInLayerTransformationMode
                        val isViewTransform = !isLayerTransform && pointerCount >= 2
                        isLayerTransform || isViewTransform
                    },
                    onGestureStart = { centroid ->
                        val currentDrawingState = currentStateState.value
                        val viewModelState = currentViewModelState.value

                        // Sync local trackers with current state
                        activeZoom = currentZoomState.value
                        activeAngle = currentAngleState.value
                        activeTranslation = currentTranslationState.value

                        viewModelState.onEvent(DrawingEvent.CancelDrawing)

                        if (currentDrawingState.isInLayerTransformationMode && currentDrawingState.layerTransformPivot == null) {
                            val currentRenderScale = currentRenderScaleState.value
                            val centroidCanvas =
                                (centroid.rotateBy(-activeAngle) / activeZoom) / currentRenderScale
                            viewModelState.onEvent(DrawingEvent.UpdateLayerTransformPivot(centroidCanvas))
                        }
                    },
                    onGesture = { centroid, pan, zoomChangeStep, rotationChangeStep ->
                        val currentDrawingState = currentStateState.value
                        val isLayerTransform = currentDrawingState.isInLayerTransformationMode

                        // Determine active values (Layer uses UI state directly as it doesn't have the jitter issue same way, or does it?
                        // Actually layer transform state is in Redux store (ViewModel), while view transform is local state.
                        // We use local vars for view transform. For layer transform we despatch events.

                        if (isLayerTransform) {
                            // Uses captured view state for coordinate conversion
                            val currentRenderScale = currentRenderScaleState.value

                            val currentTransform = currentDrawingState.layerTransformState
                            val newScale = currentTransform.scale * zoomChangeStep
                            val newRotation = currentTransform.rotation + rotationChangeStep
                            // Adjust pan for view rotation/zoom and convert to canvas coordinates.
                            val correctedPan = (pan.rotateBy(-activeAngle) / activeZoom) / currentRenderScale
                            val newTranslation = currentTransform.translation + correctedPan

                            currentViewModelState.value.onEvent(
                                DrawingEvent.UpdateLayerTransform(
                                    LayerTransformState(newScale, newRotation, newTranslation)
                                )
                            )
                        } else {
                            val newZoom = activeZoom * zoomChangeStep
                            val newAngle = activeAngle + rotationChangeStep

                            val layoutX = layoutXState.value
                            val layoutY = layoutYState.value
                            val layoutPos = Offset(layoutX, layoutY)

                            val currentVisualPos = layoutPos + activeTranslation
                            val vectorToTopLeft = currentVisualPos - centroid
                            val newVector = vectorToTopLeft.rotateBy(rotationChangeStep) * zoomChangeStep
                            val newVisualPos = centroid + newVector + pan
                            val newTranslation = newVisualPos - layoutPos

                            // Update local variables for next iteration
                            activeZoom = newZoom
                            activeAngle = newAngle
                            activeTranslation = newTranslation

                            onViewTransformChange(newZoom, newAngle, newTranslation)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


