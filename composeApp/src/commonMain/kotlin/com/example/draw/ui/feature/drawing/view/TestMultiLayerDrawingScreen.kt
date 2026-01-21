package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput

@Composable
fun TestMultiLayerDrawingScreen() {
    // --- KHU VỰC STATE (Giả lập ViewModel) ---
    // Tạo sẵn 2 lớp: Lớp nền (0) và Lớp trên (1)
    val layer1Id = "L1"
    val layer2Id = "L2"

    var layers by remember {
        mutableStateOf(
            listOf(
                VectorLayer(id = layer1Id, paths = emptyList()),
                VectorLayer(id = layer2Id, paths = emptyList()),
            )
        )
    }

    // Đang chọn lớp nào để vẽ?
    var activeLayerId by remember { mutableStateOf(layer2Id) }

    // Nét đang vẽ (chưa nhấc tay)
    var currentPath by remember { mutableStateOf<DrawingPath?>(null) }
    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }
    var isEraserMode by remember { mutableStateOf(false) }

    // --- GIAO DIỆN ---
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // Màu nền giấy (dưới cùng)
            // Lớp cảm ứng nằm ở Box cha để bắt sự kiện chung
            .drawingInput(
                onDragStart = { offset ->
                    // BẮT ĐẦU: Tạo một nét mới
                    currentTouchPosition = offset
                    currentPath = DrawingPath(
                        points = listOf(offset), // Điểm đầu tiên
                        brush = SolidBrush(
                            colorArgb = Color.Red.toArgb().toLong(),
                            size = 20f,
                            opacity = 1f,
                        ),

                        )
                },
                onDrag = { offset ->
                    currentTouchPosition = offset
                    currentPath = currentPath?.copy(
                        points = currentPath!!.points + offset
                    )
                },
                onDragEnd = {
                    // QUAN TRỌNG: Lưu nét vẽ vào ĐÚNG lớp đang chọn
                    if (currentPath != null) {
                        layers = layers.map { layer ->
                            if (layer.id == activeLayerId) {
                                // Thêm nét vẽ vào danh sách của lớp này
                                layer.copy(paths = layer.paths + currentPath!!)
                            } else {
                                layer
                            }
                        }
                        currentPath = null
                        currentTouchPosition = null
                    }
                }
            )
    ) {
        // --- VẼ CÁC LỚP ---
        // Duyệt qua danh sách layers và vẽ từng cái một
        layers.forEach { layer ->
            if (layer.isVisible) {
                // Kiểm tra xem đây có phải lớp đang active không
                // Nếu đúng, ta truyền thêm currentPath vào để nó vẽ nét đang kéo
                val pathBeingDrawn = if (layer.id == activeLayerId) currentPath else null

                // Hiển thị cục tẩy indicator chỉ khi ở lớp active
                val touchPos = if (layer.id == activeLayerId) currentTouchPosition else null

                DrawingCanvas(
                    paths = layer.paths,
                    currentPath = pathBeingDrawn,
                    isEraserMode = isEraserMode,
                    currentTouchPosition = touchPos,
                    brushSize = 20f,
                    modifier = Modifier
                        .fillMaxSize()
                        // Áp dụng độ mờ của cả layer (nếu cần)
                        .graphicsLayer { alpha = layer.opacity }
                )
            }
        }

        // --- UI ĐIỀU KHIỂN (Chọn lớp) ---
        Column(Modifier.padding(16.dp)) {
            Text("Chọn lớp để vẽ:")
            Row {
                Button(
                    onClick = { activeLayerId = layer1Id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeLayerId == layer1Id) Color.Blue else Color.Gray
                    )
                ) { Text("Lớp dưới") }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = { activeLayerId = layer2Id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeLayerId == layer2Id) Color.Blue else Color.Gray
                    )
                ) { Text("Lớp trên") }
            }

            Button(onClick = { isEraserMode = !isEraserMode }) {
                Text(if (isEraserMode) "Chuyển sang Bút" else "Chuyển sang Tẩy")
            }
        }
    }
}