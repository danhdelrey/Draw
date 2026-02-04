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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import com.example.draw.data.model.shape.RectangleState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.abs

/**
 * Enum representing which handle is being interacted with
 */
internal enum class RectangleHandleType {
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
 * Rectangle overlay composable that displays the rectangle and control handles.
 * Handles all gesture interactions for manipulating the rectangle.
 *
 * Note: Drawing path preview is handled by DrawingCanvas for proper eraser support.
 * This overlay is placed outside the canvas box to receive input from anywhere on screen.
 *
 * @param rectangleState Current rectangle state
 * @param renderScale Scale factor for rendering
 * @param inputScale Scale factor for input coordinates
 * @param canvasOffsetX X offset of canvas from container top-left
 * @param canvasOffsetY Y offset of canvas from container top-left
 * @param onUpdateRectangle Callback when rectangle state changes
 * @param onExitRectangleMode Callback when user taps the exit handle
 * @param onStartDrawing Callback when user starts drawing on canvas
 * @param onUpdateDrawing Callback when user continues drawing
 * @param onEndDrawing Callback when user finishes drawing
 */
@Composable
fun RectangleOverlay(
    rectangleState: RectangleState,
    renderScale: Float,
    inputScale: Float,
    canvasOffsetX: Float,
    canvasOffsetY: Float,
    onUpdateRectangle: (RectangleState) -> Unit,
    onExitRectangleMode: () -> Unit,
    onStartDrawing: (Offset) -> Unit,
    onUpdateDrawing: (Offset) -> Unit,
    onEndDrawing: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeHandle by remember { mutableStateOf(RectangleHandleType.NONE) }
    var dragStartAngle by remember { mutableStateOf(0f) }
    var initialRectangleState by remember { mutableStateOf(rectangleState) }
    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }

    // Keep a mutable reference to the latest rectangle state for use in gestures
    var currentRectangleState by remember { mutableStateOf(rectangleState) }

    // Track the previous drawing angle for smooth stroke generation
    var previousDrawingTheta by remember { mutableStateOf<Float?>(null) } // Add this state property

    // Update the current rectangle state when prop changes
    currentRectangleState = rectangleState

    // Helper function to convert screen coordinates to canvas coordinates
    fun screenToCanvas(screenOffset: Offset): Offset {
        return (screenOffset - Offset(canvasOffsetX, canvasOffsetY)) * inputScale
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Canvas for rendering rectangle and handles only
        // Drawing path is rendered by DrawingCanvas for proper eraser support
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Translate to canvas position, then scale
            translate(left = canvasOffsetX, top = canvasOffsetY) {
                scale(scale = renderScale, pivot = Offset.Zero) {
                    // Draw the rectangle
                    drawRectangle(rectangleState)

                    // Draw control handles
                    drawControlHandles(rectangleState)
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
                        val handles = currentRectangleState.getHandlePositions()

                        // Check if tapped on bottom (exit) handle
                        if (isNearHandle(scaledOffset, handles.bottom)) {
                            onExitRectangleMode()
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val scaledOffset = screenToCanvas(offset)
                            val handles = currentRectangleState.getHandlePositions()
                            initialRectangleState = currentRectangleState
                            dragStartPosition = scaledOffset

                            // Determine which handle is being dragged
                            activeHandle = when {
                                isNearHandle(scaledOffset, handles.center) -> RectangleHandleType.CENTER_MOVE
                                isNearHandle(scaledOffset, handles.left) -> RectangleHandleType.LEFT_ROTATE
                                isNearHandle(scaledOffset, handles.right) -> RectangleHandleType.RIGHT_ASPECT
                                isNearHandle(scaledOffset, handles.top) -> RectangleHandleType.TOP_SCALE
                                isNearHandle(scaledOffset, handles.bottom) -> RectangleHandleType.BOTTOM_EXIT
                                else -> RectangleHandleType.CANVAS_DRAW
                            }

                            if (activeHandle == RectangleHandleType.LEFT_ROTATE) {
                                dragStartAngle = currentRectangleState.angleToPoint(scaledOffset)
                            }

                            if (activeHandle == RectangleHandleType.CANVAS_DRAW) {
                                // Reset previous angle for new stroke
                                previousDrawingTheta = null
                                // Project point onto rectangle and start drawing using angle tracking
                                val (projectedPoint, theta) = currentRectangleState.projectPointToPerimeterWithAngle(scaledOffset, null)
                                previousDrawingTheta = theta
                                onStartDrawing(projectedPoint)
                            }
                        },
                        onDrag = { change, _ ->
                            val scaledOffset = screenToCanvas(change.position)

                            when (activeHandle) {
                                RectangleHandleType.CENTER_MOVE -> {
                                    val delta = scaledOffset - dragStartPosition
                                    val newCenter = initialRectangleState.center + delta
                                    onUpdateRectangle(initialRectangleState.updateCenter(newCenter))
                                }
                                RectangleHandleType.LEFT_ROTATE -> {
                                    val currentAngle = initialRectangleState.angleToPoint(scaledOffset)
                                    val angleDelta = currentAngle - dragStartAngle
                                    val newRotation = initialRectangleState.rotation + angleDelta
                                    onUpdateRectangle(initialRectangleState.updateRotation(newRotation))
                                }
                                RectangleHandleType.RIGHT_ASPECT -> {
                                    // Adjust width based on horizontal drag distance relative to local rotation
                                    val dx = scaledOffset.x - initialRectangleState.center.x
                                    val dy = scaledOffset.y - initialRectangleState.center.y

                                    // Rotate dx/dy back to local rectangle coordinates
                                    val rotation = initialRectangleState.rotation
                                    val localX = dx * cos(-rotation) - dy * sin(-rotation)

                                    // Use absolute distance from center for new width (halfWidth * 2)
                                    val newWidth = abs(localX) * 2f

                                    onUpdateRectangle(initialRectangleState.updateDimensions(newWidth, initialRectangleState.height))
                                }
                                RectangleHandleType.TOP_SCALE -> {
                                    // Calculate distance from center
                                    val initialDistance = (dragStartPosition - initialRectangleState.center).getDistance()
                                    val currentDistance = (scaledOffset - initialRectangleState.center).getDistance()

                                    if (initialDistance > 0) {
                                        val scaleFactor = currentDistance / initialDistance
                                        onUpdateRectangle(initialRectangleState.scale(scaleFactor))
                                    }
                                }
                                RectangleHandleType.CANVAS_DRAW -> {
                                    // Project point onto rectangle perimeter with angle tracking
                                    val (projectedPoint, theta) = currentRectangleState.projectPointToPerimeterWithAngle(
                                        scaledOffset,
                                        previousDrawingTheta
                                    )
                                    previousDrawingTheta = theta
                                    onUpdateDrawing(projectedPoint)
                                }
                                RectangleHandleType.BOTTOM_EXIT, RectangleHandleType.NONE -> {
                                    // Do nothing
                                }
                            }
                        },
                        onDragEnd = {
                            if (activeHandle == RectangleHandleType.CANVAS_DRAW) {
                                // Reset previous angle when stroke ends
                                previousDrawingTheta = null
                                onEndDrawing()
                            }
                            activeHandle = RectangleHandleType.NONE
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
 * Draw the rectangle outline
 */
private fun DrawScope.drawRectangle(rectangleState: RectangleState) {
    val rotationDegrees = rectangleState.rotation * (180f / kotlin.math.PI.toFloat())
    rotate(
        degrees = rotationDegrees,
        pivot = rectangleState.center
    ) {
        drawRect(
            color = Color.Black,
            topLeft = Offset(
                rectangleState.center.x - rectangleState.width / 2f,
                rectangleState.center.y - rectangleState.height / 2f
            ),
            size = Size(
                rectangleState.width,
                rectangleState.height
            ),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Draw control handles around the rectangle
 */
private fun DrawScope.drawControlHandles(rectangleState: RectangleState) {
    val handles = rectangleState.getHandlePositions()

    // Left handle - Rotate (circular with rotation icon hint)
    drawHandle(handles.left, Color.Blue, "R")

    // Right handle - Width/Aspect (horizontal arrow hint)
    drawHandle(handles.right, Color.Green, "W")

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
private fun DrawScope.drawHandle(position: Offset, color: Color, @Suppress("UNUSED_PARAMETER") label: String) {
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
    // Use label to avoid unused parameter error if we ever decide to draw text
    // For now it's just a hint for the developer reading the code
}

/**
 * Draw the exit handle (Red X)
 */
private fun DrawScope.drawExitHandle(position: Offset) {
    drawCircle(
        color = Color.Red,
        radius = HANDLE_RADIUS,
        center = position,
        style = Stroke(width = 3f)
    )
    // Draw X
    val xSize = HANDLE_RADIUS * 0.5f
    drawLine(
        color = Color.Red,
        start = position - Offset(xSize, xSize),
        end = position + Offset(xSize, xSize),
        strokeWidth = 3f
    )
    drawLine(
        color = Color.Red,
        start = position - Offset(-xSize, xSize),
        end = position + Offset(-xSize, xSize),
        strokeWidth = 3f
    )
}

/**
 * Draw the move handle (Center crosshair)
 */
private fun DrawScope.drawMoveHandle(position: Offset) {
    drawCircle(
        color = Color.Gray,
        radius = HANDLE_RADIUS * 0.8f,
        center = position,
        style = Stroke(width = 2f)
    )
    val crossSize = HANDLE_RADIUS * 0.5f
    drawLine(
        color = Color.Gray,
        start = position - Offset(crossSize, 0f),
        end = position + Offset(crossSize, 0f),
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Gray,
        start = position - Offset(0f, crossSize),
        end = position + Offset(0f, crossSize),
        strokeWidth = 2f
    )
}
