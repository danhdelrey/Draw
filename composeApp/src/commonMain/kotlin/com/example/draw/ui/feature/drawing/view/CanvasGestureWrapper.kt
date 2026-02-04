package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.example.draw.data.model.transform.LayerTransformState
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
                awaitEachGesture {
                    var transformStarted = false
                    // Use PointerEventPass.Initial to observe events BEFORE the child (DrawingCanvas)
                    // This allows us to intercept 2-finger gestures and consume them so the child stops drawing.
                    awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val pointerCount = event.changes.size

                        // In layer mode, we handle ALL gestures (pan, zoom, rotate).
                        // In normal mode, we only handle multi-touch (view panning/zooming).
                        val currentDrawingState = currentStateState.value
                        val isLayerTransform = currentDrawingState.isInLayerTransformationMode
                        val isViewTransform = !isLayerTransform && pointerCount >= 2

                        if (isLayerTransform || isViewTransform) {
                            // Intercept! Consume all changes so child sees them as consumed in Main pass.
                            event.changes.forEach {
                                it.consume()
                            }

                            val centroid = event.calculateCentroid(useCurrent = false)
                            val pan = event.calculatePan()
                            val zoomChangeStep = event.calculateZoom()
                            val rotationChangeStep = event.calculateRotation()

                            // Access current local state
                            val activeZoom = currentZoomState.value
                            val activeAngle = currentAngleState.value
                            val activeTranslation = currentTranslationState.value
                            val currentRenderScale = currentRenderScaleState.value

                            if (!transformStarted) {
                                transformStarted = true
                                currentViewModelState.value.onEvent(DrawingEvent.CancelDrawing)

                                if (isLayerTransform && currentDrawingState.layerTransformPivot == null) {
                                    val centroidCanvas =
                                        (centroid.rotateBy(-activeAngle) / activeZoom) / currentRenderScale
                                    currentViewModelState.value.onEvent(DrawingEvent.UpdateLayerTransformPivot(centroidCanvas))
                                }
                            }

                            // Apply changes
                            if (zoomChangeStep != 1f || rotationChangeStep != 0f || pan != Offset.Zero) {
                                if (isLayerTransform) {
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

                                    onViewTransformChange(newZoom, newAngle, newTranslation)
                                }
                            }
                        } else {
                            transformStarted = false
                        }

                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private fun Offset.rotateBy(angleDegrees: Float): Offset {
    val angleRad = angleDegrees * PI / 180
    val cosVal = cos(angleRad)
    val sinVal = sin(angleRad)
    return Offset(
        (x * cosVal - y * sinVal).toFloat(),
        (x * sinVal + y * cosVal).toFloat()
    )
}

