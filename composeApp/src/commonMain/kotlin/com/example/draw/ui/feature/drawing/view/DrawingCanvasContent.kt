package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.lifecycle.viewmodel.compose.viewModel
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
    Box(

        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White)
                .graphicsLayer(alpha = 0.99f)
                .drawWithContent {
                    // 1. Ra lệnh cho graphicsLayer ghi lại nội dung của Box này
                    rootGraphicsLayer.record {
                        // "this@drawWithContent" chính là lệnh vẽ nội dung gốc (nền trắng + layer con)
                        this@drawWithContent.drawContent()
                    }
                    // 2. Vẽ cái graphicsLayer đó ra màn hình để mắt người dùng nhìn thấy
                    drawLayer(rootGraphicsLayer)
                }
        ){
            // --- VẼ CÁC LỚP ---
            // Duyệt qua danh sách layers và vẽ từng cái một
            state.currentLayers.forEach { layer ->
                if(layer.isVisible && layer is VectorLayer) {
                    // Kiểm tra xem đây có phải lớp đang active không
                    // Nếu đúng, ta truyền thêm currentPath vào để nó vẽ nét đang kéo
                    val pathBeingDrawn = if (layer.id == state.currentActiveLayer.id) state.currentDrawingPath else null

                    // Hiển thị cục tẩy indicator chỉ khi ở lớp active
                    val touchPos = if (layer.id == state.currentActiveLayer.id) state.currentTouchPosition else null

                    DrawingCanvas(
                        paths = layer.paths,
                        currentPath = pathBeingDrawn,
                        isEraserMode = false,
                        currentTouchPosition = touchPos,
                        brushSize = state.currentBrush.size,
                        modifier = Modifier
                            .fillMaxSize()
                            // Áp dụng độ mờ của cả layer (nếu cần)
                            .graphicsLayer { alpha = layer.opacity }
                            .drawingInput(
                                onDragStart = { offset ->
                                    viewModel.onEvent(
                                        DrawingEvent.StartDrawing(
                                            currentTouchPosition = offset,
                                        )
                                    )
                                },
                                onDrag = { offset ->
                                    // DI CHUYỂN: Thêm điểm vào nét đang vẽ
                                    viewModel.onEvent(
                                        DrawingEvent.UpdateDrawing(
                                            currentTouchPosition = offset,
                                        )
                                    )
                                },
                                onDragEnd = {
                                    // KẾT THÚC: Lưu nét vẽ vào danh sách chính
                                    viewModel.onEvent(DrawingEvent.EndDrawing)
                                }
                            )
                    )
                }
            }
        }


    }
}