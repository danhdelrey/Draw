package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.transform.LayerTransformState
import com.example.draw.ui.common.canvasOverlay.RectBoundOverlay
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.EllipseOverlay
import com.example.draw.ui.feature.drawing.component.RectangleOverlay
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DrawingCanvasContent(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    rootGraphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current

        // Calculate scale to fit canvas inside screen (contain behavior)
        val canvasWidth = state.canvas.width
        val canvasHeight = state.canvas.height

        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        val scale = minOf(
            availableWidth / canvasWidth,
            availableHeight / canvasHeight
        )

        // Scaling factors
        val inputScale = 1f / scale

        val displayedWidth = canvasWidth * scale
        val displayedHeight = canvasHeight * scale

        val activeTransformLayer = state.layers
            .firstOrNull { it.id == (state.transformLayerId ?: state.canvas.activeLayerId) }
            as? VectorLayer


        val pivot = state.layerTransformPivot
        val layerTransformOrigin = if (pivot != null && canvasWidth != 0f && canvasHeight != 0f) {
            TransformOrigin(pivot.x / canvasWidth, pivot.y / canvasHeight)
        } else {
            TransformOrigin(0.5f, 0.5f)
        }

        // Calculate canvas offset from top-left of the container (Alignment.Center places it here)
        val layoutOffsetX = (availableWidth - displayedWidth) / 2f
        val layoutOffsetY = (availableHeight - displayedHeight) / 2f

        // --- TRANSFORMATION STATE ---
        var zoom by remember { mutableStateOf(1f) }
        var angle by remember { mutableStateOf(0f) }
        var translation by remember { mutableStateOf(Offset.Zero) }

        // Helper for rotation
        fun Offset.rotateBy(angleDegrees: Float): Offset {
            val angleRad = angleDegrees * PI / 180
            val cosVal = cos(angleRad)
            val sinVal = sin(angleRad)
            return Offset(
                (x * cosVal - y * sinVal).toFloat(),
                (x * sinVal + y * cosVal).toFloat()
            )
        }

        val currentState by rememberUpdatedState(state)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // View Transform (Initial Pass)
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        do {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                            val pointerCount = event.changes.size
                            val currentDrawingState = currentState

                            val isViewTransform = !currentDrawingState.isInLayerTransformationMode && pointerCount >= 2

                            if (isViewTransform) {
                                event.changes.forEach { it.consume() }
                                viewModel.onEvent(DrawingEvent.CancelDrawing)

                                val centroid = event.calculateCentroid(useCurrent = false)
                                val pan = event.calculatePan()
                                val zoomChangeStep = event.calculateZoom()
                                val rotationChangeStep = event.calculateRotation()

                                if (zoomChangeStep != 1f || rotationChangeStep != 0f || pan != Offset.Zero) {
                                    val newZoom = zoom * zoomChangeStep
                                    zoom = newZoom
                                    angle += rotationChangeStep

                                    val layoutPos = Offset(layoutOffsetX, layoutOffsetY)
                                    val currentVisualPos = layoutPos + translation
                                    val vectorToTopLeft = currentVisualPos - centroid
                                    val newVector = vectorToTopLeft.rotateBy(rotationChangeStep) * zoomChangeStep
                                    val newVisualPos = centroid + newVector + pan
                                    translation = newVisualPos - layoutPos
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(Unit) {
                    // Layer Transform (Main Pass)
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var transformStarted = false

                        do {
                            val event = awaitPointerEvent()
                            val currentDrawingState = currentState
                            val isLayerTransform = currentDrawingState.isInLayerTransformationMode
                            // Check if child (e.g. Button) consumed the event
                            val isConsumed = event.changes.any { it.isConsumed }

                            if (isLayerTransform && !isConsumed) {
                                val centroid = event.calculateCentroid(useCurrent = false)
                                val pan = event.calculatePan()
                                val zoomChangeStep = event.calculateZoom()
                                val rotationChangeStep = event.calculateRotation()

                                if (!transformStarted) {
                                    transformStarted = true
                                    viewModel.onEvent(DrawingEvent.CancelDrawing)

                                    if (currentDrawingState.layerTransformPivot == null) {
                                        val centroidCanvas =
                                            (centroid.rotateBy(-angle) / zoom) / scale
                                        viewModel.onEvent(DrawingEvent.UpdateLayerTransformPivot(centroidCanvas))
                                    }
                                }

                                if (zoomChangeStep != 1f || rotationChangeStep != 0f || pan != Offset.Zero) {
                                    val currentTransform = currentDrawingState.layerTransformState
                                    val newScale = currentTransform.scale * zoomChangeStep
                                    val newRotation = currentTransform.rotation + rotationChangeStep
                                    // Adjust pan for view rotation/zoom and convert to canvas coordinates.
                                    val correctedPan = (pan.rotateBy(-angle) / zoom) / scale
                                    val newTranslation = currentTransform.translation + correctedPan

                                    viewModel.onEvent(DrawingEvent.UpdateLayerTransform(
                                        LayerTransformState(newScale, newRotation, newTranslation)
                                    ))
                                }
                                event.changes.forEach { it.consume() }
                            } else {
                                transformStarted = false
                            }

                        } while (event.changes.any { it.pressed })
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = with(density) { displayedWidth.toDp() },
                        height = with(density) { displayedHeight.toDp() }
                    )
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = translation.x
                        translationY = translation.y
                        rotationZ = angle
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .background(Color.White)
                    .drawWithContent {
                        rootGraphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(rootGraphicsLayer)
                    }
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
                                            state.layerTransformState.translation.x * scale
                                        translationY =
                                            state.layerTransformState.translation.y * scale
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
                                renderScale = scale,
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

                // --- TRANSFORM LAYER MODE INDICATOR ---
                RectBoundOverlay(
                    shouldShowRectOverlay = true,
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
                        renderScale = scale,
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
                        renderScale = scale,
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
        }
    }
}
