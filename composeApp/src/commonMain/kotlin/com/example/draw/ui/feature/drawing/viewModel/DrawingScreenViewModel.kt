package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import cafe.adriel.voyager.core.model.ScreenModel
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.layer.VectorLayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.plus

class DrawingScreenViewModel : ScreenModel {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    

    fun onEvent(event: DrawingEvent){
        when(event){
            is DrawingEvent.StartDrawing -> {
                // Khi người dùng bắt đầu chạm tay / bút xuống màn hình
                // → Khởi tạo một nét vẽ mới (currentDrawingPath)

                _state.value = _state.value.copy(
                    // Tạo DrawingPath mới hoàn toàn
                    // Không sửa path cũ → đảm bảo state là immutable
                    currentDrawingPath = DrawingPath(
                        // Danh sách điểm ban đầu của nét vẽ
                        // Chỉ có 1 điểm duy nhất: vị trí chạm đầu tiên
                        points = listOf(event.currentTouchPosition),

                        // Lưu brush hiện tại vào path
                        // Việc này giúp path giữ nguyên brush kể cả khi người dùng đổi brush sau đó
                        brush = _state.value.currentBrush
                    ),

                    // Lưu lại vị trí tay hiện tại
                    // Dùng để:
                    // - vẽ preview (cursor, crosshair)
                    // - hiển thị vị trí touch trong UI
                    currentTouchPosition = event.currentTouchPosition
                )
            }


            is DrawingEvent.UpdateDrawing -> {
                // Sự kiện này được gọi liên tục khi người dùng kéo tay / bút

                // Lấy path đang được vẽ
                // Nếu chưa StartDrawing (ví dụ drag sai thứ tự) → bỏ qua
                val currentPath = _state.value.currentDrawingPath ?: return

                _state.value = _state.value.copy(
                    // Cập nhật lại path đang vẽ
                    // Không mutate path cũ → tạo bản copy mới
                    currentDrawingPath = currentPath.copy(
                        // Thêm điểm mới vào danh sách points
                        // `+` tạo ra một List mới → Compose nhận ra state thay đổi
                        points = currentPath.points + event.currentTouchPosition
                    ),

                    // Cập nhật vị trí tay hiện tại
                    // Dùng cho việc vẽ cursor / indicator theo thời gian thực
                    currentTouchPosition = event.currentTouchPosition
                )
            }


            is DrawingEvent.EndDrawing -> {
                // Sự kiện xảy ra khi người dùng nhấc tay / bút lên

                // Lấy path đang vẽ
                // Nếu không có (edge case) → bỏ qua
                val currentPath = _state.value.currentDrawingPath ?: return
                val layers = _state.value.currentLayers.map { layer ->
                    if (
                        layer.id == _state.value.currentActiveLayer.id &&
                        layer is VectorLayer
                    ) {
                        layer.copy(
                            paths = layer.paths + currentPath
                        )
                    } else {
                        layer
                    }
                }


                _state.value = _state.value.copy(
                    // Thêm path vừa vẽ xong vào danh sách các nét đã hoàn thành
                    // `+ currentPath` tạo List mới → trigger recomposition
                    currentLayers = layers,

                    // Xóa path tạm thời đang vẽ
                    // Canvas sẽ không còn vẽ path này nữa
                    currentDrawingPath = null,

                    // Xóa vị trí tay
                    // Thường dùng để ẩn cursor / touch indicator
                    currentTouchPosition = null
                )

                _state.value.currentLayers


            }

            is DrawingEvent.ChangeBrush -> {
                // Thay đổi cài đặt bút vẽ hiện tại
                _state.value = _state.value.copy(
                    currentBrush = event.brush
                )
            }


        }
    }


}