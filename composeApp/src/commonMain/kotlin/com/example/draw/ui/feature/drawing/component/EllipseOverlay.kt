package com.example.draw.ui.feature.drawing.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import com.example.draw.data.model.shape.EllipseState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Enum representing which handle is being interacted with
 */
private enum class HandleType {
    NONE,
    LEFT_ROTATE,
    RIGHT_ASPECT,
    TOP_SCALE,
    BOTTOM_EXIT,
    CENTER_MOVE,
    CANVAS_DRAW
}

/**
 * Size of control handles in pixels
 */
private const val HANDLE_RADIUS = 30f
private const val HANDLE_HIT_RADIUS = 60f

/**
 * Ellipse overlay composable that displays the ellipse and control handles.
 * Handles all gesture interactions for manipulating the ellipse.
 *
 * Note: Drawing path preview is handled by DrawingCanvas for proper eraser support.
 * This overlay is placed outside the canvas box to receive input from anywhere on screen.
 *
 * @param ellipseState Current ellipse state
 * @param renderScale Scale factor for rendering
 * @param inputScale Scale factor for input coordinates
 * @param canvasOffsetX X offset of canvas from container top-left
 * @param canvasOffsetY Y offset of canvas from container top-left
 * @param onUpdateEllipse Callback when ellipse state changes
 * @param onExitEllipseMode Callback when user taps the exit handle
 * @param onStartDrawing Callback when user starts drawing on canvas
 * @param onUpdateDrawing Callback when user continues drawing
 * @param onEndDrawing Callback when user finishes drawing
 */
@Composable
fun EllipseOverlay(
    ellipseState: EllipseState,
    renderScale: Float,
    inputScale: Float,
    canvasOffsetX: Float,
    canvasOffsetY: Float,
    onUpdateEllipse: (EllipseState) -> Unit,
    onExitEllipseMode: () -> Unit,
    onStartDrawing: (Offset) -> Unit,
    onUpdateDrawing: (Offset) -> Unit,
    onEndDrawing: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeHandle by remember { mutableStateOf(HandleType.NONE) }
    var dragStartAngle by remember { mutableStateOf(0f) }
    var initialEllipseState by remember { mutableStateOf(ellipseState) }
    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }

    // Keep a mutable reference to the latest ellipse state for use in gestures
    var currentEllipseState by remember { mutableStateOf(ellipseState) }

    // Track the previous drawing angle for smooth stroke generation
    var previousDrawingTheta by remember { mutableStateOf<Float?>(null) }

    // Update the current ellipse state when prop changes
    currentEllipseState = ellipseState

    // Helper function to convert screen coordinates to canvas coordinates
    fun screenToCanvas(screenOffset: Offset): Offset {
        return (screenOffset - Offset(canvasOffsetX, canvasOffsetY)) * inputScale
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Canvas for rendering ellipse and handles only
        // Drawing path is rendered by DrawingCanvas for proper eraser support
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Translate to canvas position, then scale
            translate(left = canvasOffsetX, top = canvasOffsetY) {
                scale(scale = renderScale, pivot = Offset.Zero) {
                    // Draw the ellipse
                    drawEllipse(ellipseState)

                    // Draw control handles
                    drawControlHandles(ellipseState)
                }
            }
        }

        // Input layer for gestures - use Unit as key to avoid restarting mid-gesture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val scaledOffset = screenToCanvas(tapOffset)
                        val handles = currentEllipseState.getHandlePositions()

                        // Check if tapped on bottom (exit) handle
                        if (isNearHandle(scaledOffset, handles.bottom)) {
                            onExitEllipseMode()
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val scaledOffset = screenToCanvas(offset)
                            val handles = currentEllipseState.getHandlePositions()
                            initialEllipseState = currentEllipseState
                            dragStartPosition = scaledOffset

                            // Determine which handle is being dragged
                            activeHandle = when {
                                isNearHandle(scaledOffset, handles.center) -> HandleType.CENTER_MOVE
                                isNearHandle(scaledOffset, handles.left) -> HandleType.LEFT_ROTATE
                                isNearHandle(scaledOffset, handles.right) -> HandleType.RIGHT_ASPECT
                                isNearHandle(scaledOffset, handles.top) -> HandleType.TOP_SCALE
                                isNearHandle(scaledOffset, handles.bottom) -> HandleType.BOTTOM_EXIT
                                else -> HandleType.CANVAS_DRAW
                            }

                            if (activeHandle == HandleType.LEFT_ROTATE) {
                                dragStartAngle = currentEllipseState.angleToPoint(scaledOffset)
                            }

                            if (activeHandle == HandleType.CANVAS_DRAW) {
                                // Reset previous angle for new stroke
                                previousDrawingTheta = null
                                // Project point onto ellipse and start drawing
                                val (projectedPoint, theta) = currentEllipseState.projectPointToPerimeterWithAngle(scaledOffset, null)
                                previousDrawingTheta = theta
                                onStartDrawing(projectedPoint)
                            }
                        },
                        onDrag = { change, _ ->
                            val scaledOffset = screenToCanvas(change.position)

                            when (activeHandle) {
                                HandleType.CENTER_MOVE -> {
                                    val delta = scaledOffset - dragStartPosition
                                    val newCenter = initialEllipseState.center + delta
                                    onUpdateEllipse(initialEllipseState.updateCenter(newCenter))
                                }
                                HandleType.LEFT_ROTATE -> {
                                    val currentAngle = initialEllipseState.angleToPoint(scaledOffset)
                                    val angleDelta = currentAngle - dragStartAngle
                                    val newRotation = initialEllipseState.rotation + angleDelta
                                    onUpdateEllipse(initialEllipseState.updateRotation(newRotation))
                                }
                                HandleType.RIGHT_ASPECT -> {
                                    // Adjust aspect ratio based on horizontal drag distance
                                    val dx = scaledOffset.x - initialEllipseState.center.x
                                    val dy = scaledOffset.y - initialEllipseState.center.y

                                    // Rotate dx/dy back to local ellipse coordinates
                                    val rotation = initialEllipseState.rotation
                                    val localX = dx * cos(-rotation) - dy * sin(-rotation)

                                    // Use absolute distance from center for new radii
                                    val newRadiusX = kotlin.math.abs(localX).coerceAtLeast(20f)
                                    val newRadiusY = initialEllipseState.radiusY

                                    onUpdateEllipse(initialEllipseState.updateRadii(newRadiusX, newRadiusY))
                                }
                                HandleType.TOP_SCALE -> {
                                    // Calculate distance from center
                                    val initialDistance = (dragStartPosition - initialEllipseState.center).getDistance()
                                    val currentDistance = (scaledOffset - initialEllipseState.center).getDistance()

                                    if (initialDistance > 0) {
                                        val scaleFactor = currentDistance / initialDistance
                                        val newRadiusX = (initialEllipseState.radiusX * scaleFactor).coerceIn(20f, 1000f)
                                        val newRadiusY = (initialEllipseState.radiusY * scaleFactor).coerceIn(20f, 1000f)
                                        onUpdateEllipse(initialEllipseState.updateRadii(newRadiusX, newRadiusY))
                                    }
                                }
                                HandleType.CANVAS_DRAW -> {
                                    // Project point onto ellipse perimeter with angle tracking for stability
                                    val (projectedPoint, theta) = currentEllipseState.projectPointToPerimeterWithAngle(
                                        scaledOffset,
                                        previousDrawingTheta
                                    )
                                    previousDrawingTheta = theta
                                    onUpdateDrawing(projectedPoint)
                                }
                                HandleType.BOTTOM_EXIT, HandleType.NONE -> {
                                    // Do nothing for exit handle drag or no active handle
                                }
                            }
                        },
                        onDragEnd = {
                            if (activeHandle == HandleType.CANVAS_DRAW) {
                                // Reset previous angle when stroke ends
                                previousDrawingTheta = null
                                onEndDrawing()
                            }
                            activeHandle = HandleType.NONE
                        }
                    )
                }
        )
    }
}

/**
 * Check if a point is near a handle
 */
private fun isNearHandle(point: Offset, handleCenter: Offset): Boolean {
    return (point - handleCenter).getDistance() <= HANDLE_HIT_RADIUS
}

/**
 * Draw the ellipse outline
 */
private fun DrawScope.drawEllipse(ellipseState: EllipseState) {
    // Convert radians to degrees: degrees = radians * (180 / PI)
    val rotationDegrees = ellipseState.rotation * (180f / kotlin.math.PI.toFloat())
    rotate(
        degrees = rotationDegrees,
        pivot = ellipseState.center
    ) {
        drawOval(
            color = Color.Black,
            topLeft = Offset(
                ellipseState.center.x - ellipseState.radiusX,
                ellipseState.center.y - ellipseState.radiusY
            ),
            size = androidx.compose.ui.geometry.Size(
                ellipseState.radiusX * 2,
                ellipseState.radiusY * 2
            ),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Draw control handles around the ellipse
 */
private fun DrawScope.drawControlHandles(ellipseState: EllipseState) {
    val handles = ellipseState.getHandlePositions()

    // Left handle - Rotate (circular with rotation icon hint)
    drawHandle(handles.left, Color.Blue, "R")

    // Right handle - Aspect ratio (horizontal arrow hint)
    drawHandle(handles.right, Color.Green, "A")

    // Top handle - Scale (diagonal arrows hint)
    drawHandle(handles.top, Color.Magenta, "S")

    // Bottom handle - Exit (X mark)
    drawExitHandle(handles.bottom)

    // Center handle - Move (crosshair)
    drawMoveHandle(handles.center)
}

/**
 * Draw a standard control handle
 */
private fun DrawScope.drawHandle(position: Offset, color: Color, label: String) {
    // Outer circle
    drawCircle(
        color = color,
        radius = HANDLE_RADIUS,
        center = position,
        style = Stroke(width = 3f)
    )
    // Inner fill
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = HANDLE_RADIUS,
        center = position
    )
}

/**
 * Draw the exit handle with an X mark
 */
private fun DrawScope.drawExitHandle(position: Offset) {
    // Circle background
    drawCircle(
        color = Color.Red,
        radius = HANDLE_RADIUS,
        center = position,
        style = Stroke(width = 3f)
    )
    drawCircle(
        color = Color.Red.copy(alpha = 0.3f),
        radius = HANDLE_RADIUS,
        center = position
    )

    // X mark
    val offset = HANDLE_RADIUS * 0.5f
    drawLine(
        color = Color.Red,
        start = Offset(position.x - offset, position.y - offset),
        end = Offset(position.x + offset, position.y + offset),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.Red,
        start = Offset(position.x + offset, position.y - offset),
        end = Offset(position.x - offset, position.y + offset),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}

/**
 * Draw the move handle with a crosshair
 */
private fun DrawScope.drawMoveHandle(position: Offset) {
    // Circle background
    drawCircle(
        color = Color.DarkGray,
        radius = HANDLE_RADIUS,
        center = position,
        style = Stroke(width = 3f)
    )
    drawCircle(
        color = Color.DarkGray.copy(alpha = 0.3f),
        radius = HANDLE_RADIUS,
        center = position
    )

    // Crosshair
    val offset = HANDLE_RADIUS * 0.6f
    drawLine(
        color = Color.DarkGray,
        start = Offset(position.x - offset, position.y),
        end = Offset(position.x + offset, position.y),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.DarkGray,
        start = Offset(position.x, position.y - offset),
        end = Offset(position.x, position.y + offset),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}


