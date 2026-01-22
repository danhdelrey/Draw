package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
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
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

@Composable
fun DrawingCanvasContent(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    rootGraphicsLayer: GraphicsLayer
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val viewWidthPx = with(density) { maxWidth.toPx() }

        // Tỷ lệ Input: Màn hình -> 1080p (để lưu vào DB)
        val inputScale = CanvasConfig.FIXED_WIDTH / viewWidthPx

        // Tỷ lệ Render: 1080p -> Màn hình (để hiển thị)
        val renderScale = viewWidthPx / CanvasConfig.FIXED_WIDTH

        Box(
            modifier = Modifier
                .aspectRatio(CanvasConfig.FIXED_WIDTH / CanvasConfig.FIXED_HEIGHT)
                .fillMaxSize() // Luôn lấp đầy width của cha (Screen width)
                .background(Color.White)
                .drawWithContent {
                    rootGraphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(rootGraphicsLayer)
                }
        ) {
            state.currentLayers.forEach { layer ->
                if (layer.isVisible && layer is VectorLayer) {
                    val pathBeingDrawn = if (layer.id == state.currentActiveLayer.id) state.currentDrawingPath else null
                    val touchPos = if (layer.id == state.currentActiveLayer.id) state.currentTouchPosition else null

                    // --- LAYER HIỂN THỊ ---
                    Box(
                        modifier = Modifier
                            .fillMaxSize() // Khớp hoàn toàn với Box cha -> Tọa độ (0,0) trùng nhau tuyệt đối
                            .graphicsLayer {
                                alpha = layer.opacity
                                // KHÔNG set scaleX, scaleY ở đây nữa để tránh lệch Layout

                                // Vẫn giữ cái này để Fix lỗi Eraser xuyên thấu
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                    ) {
                        DrawingCanvas(
                            paths = layer.paths,
                            currentPath = pathBeingDrawn,
                            isEraserMode = false, // UI Logic
                            currentTouchPosition = touchPos,
                            brushSize = state.currentBrush.size,
                            renderScale = renderScale, // <--- QUAN TRỌNG: Truyền tỷ lệ vào để Canvas tự scale nét vẽ
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // --- LAYER INPUT ---
                    if (layer.id == state.currentActiveLayer.id) {
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
    }
}