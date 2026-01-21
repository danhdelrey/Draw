package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput


@Composable
fun DrawingTestScreen() {
    // 1. Tự tạo "bộ nhớ" tạm thời (thay thế ViewModel)
    // Danh sách các nét đã vẽ xong
    var paths by remember { mutableStateOf(listOf<DrawingPath>()) }

    // Nét đang vẽ dở (khi chưa nhấc tay lên)
    var currentPath by remember { mutableStateOf<DrawingPath?>(null) }

    // Vị trí ngón tay (để hiện cục tẩy)
    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }

    // Cài đặt bút vẽ mặc định cho bài test
    var isEraserMode by remember { mutableStateOf(false) } // Thử đổi thành true để test tẩy
    val currentBrushColor = Color.Blue
    val currentStrokeWidth = 20f

    // Giao diện
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White)
        ) {
            DrawingCanvas(
                paths = paths,
                currentPath = currentPath,
                isEraserMode = isEraserMode,
                currentTouchPosition = currentTouchPosition,
                brushSize = currentStrokeWidth,
                modifier = Modifier
                    .fillMaxSize()
                    .drawingInput(
                        onDragStart = { offset ->
                            // BẮT ĐẦU: Tạo một nét mới
                            currentTouchPosition = offset
                            currentPath = DrawingPath(
                                points = listOf(offset), // Điểm đầu tiên
                                brush = SolidBrush(
                                    colorArgb = currentBrushColor.toArgb().toLong(),
                                    size = currentStrokeWidth,
                                    opacity = 1f,
                                ),

                                )
                        },
                        onDrag = { offset ->
                            // DI CHUYỂN: Thêm điểm vào nét đang vẽ
                            currentTouchPosition = offset
                            currentPath?.let { path ->
                                // Tạo bản sao mới của path với điểm mới được thêm vào
                                currentPath = path.copy(
                                    points = path.points + offset
                                )
                            }
                        },
                        onDragEnd = {
                            // KẾT THÚC: Lưu nét vẽ vào danh sách chính
                            currentPath?.let { path ->
                                paths = paths + path // Thêm vào danh sách paths
                            }
                            currentPath = null // Xóa nét tạm
                            currentTouchPosition = null // Ẩn vị trí tay
                        }
                    )
            )

            // Thêm một nút nhỏ để bạn test chuyển đổi bút/tẩy
            Button(
                onClick = { isEraserMode = !isEraserMode },
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
            ) {
                Text(text = if (isEraserMode) "Đang chọn: Tẩy" else "Đang chọn: Bút")
            }
        }
    }
}