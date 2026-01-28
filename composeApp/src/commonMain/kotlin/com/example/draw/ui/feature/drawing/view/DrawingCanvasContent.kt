package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.EllipseOverlay
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

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
        val renderScale = scale

        val displayedWidth = canvasWidth * scale
        val displayedHeight = canvasHeight * scale

        // Calculate canvas offset from top-left of the container
        val canvasOffsetX = (availableWidth - displayedWidth) / 2f
        val canvasOffsetY = (availableHeight - displayedHeight) / 2f

        Box(
            modifier = Modifier
                .size(
                    width = with(density) { displayedWidth.toDp() },
                    height = with(density) { displayedHeight.toDp() }
                )
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
                    val pathBeingDrawn = if (isActiveLayer) state.currentDrawingPath else null
                    val touchPos = if (isActiveLayer) state.currentTouchPosition else null

                    // --- LAYER DISPLAY ---
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = layer.opacity
                                compositingStrategy = CompositingStrategy.Offscreen
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

                    // --- LAYER INPUT (only for normal mode, not ellipse mode) ---
                    if (isActiveLayer && state.ellipseMode == null) {
                        // Normal drawing mode
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawingInput(
                                    onDragStart = { offset -> viewModel.onEvent(DrawingEvent.StartDrawing(offset * inputScale)) },
                                    onDrag = { offset -> viewModel.onEvent(DrawingEvent.UpdateDrawing(offset * inputScale)) },
                                    onDragEnd = { viewModel.onEvent(DrawingEvent.EndDrawing) }
                                )
                        )
                    }
                }
            }
        }

        // --- ELLIPSE OVERLAY (outside canvas for full-screen input) ---
        val ellipseMode = state.ellipseMode
        if (ellipseMode != null) {
            EllipseOverlay(
                ellipseState = ellipseMode,
                renderScale = renderScale,
                inputScale = inputScale,
                canvasOffsetX = canvasOffsetX,
                canvasOffsetY = canvasOffsetY,
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
    }
}