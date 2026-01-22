package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.repository.ImageRepository
import com.example.draw.platform.util.toPngByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.random.Random
import kotlin.time.Clock

class DrawingScreenViewModel(
    private val imageRepository: ImageRepository
) : ScreenModel {
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

            is DrawingEvent.SaveDrawing -> {
                screenModelScope.launch {
                    val name = "drawing_${Clock.System.now().toEpochMilliseconds()}"
                    val bytes = event.imageBitmap.toPngByteArray()
                    val result = imageRepository.saveImage(bytes, name)

                    if (result){
                        println("Lưu hình ảnh thành công")
                    } else {
                        println("Lưu hình ảnh thất bại")
                    }
                }
            }

            is DrawingEvent.ChangeBrush -> {
                // Thay đổi cài đặt bút vẽ hiện tại
                _state.value = _state.value.copy(
                    currentBrush = event.brush
                )
            }

            is DrawingEvent.AddLayer -> {
                val newLayer = VectorLayer(
                    id = Random.nextLong().toString(),
                )
                _state.value = _state.value.copy(
                    currentLayers = _state.value.currentLayers + newLayer,
                    currentActiveLayer = newLayer
                )
            }
            is DrawingEvent.DeleteLayer -> {
                if (event.layer.id == "default_layer") return

                val updatedLayers =
                    _state.value.currentLayers.filter { it.id != event.layer.id }

                val newActiveLayer =
                    if (_state.value.currentActiveLayer.id == event.layer.id) {
                        updatedLayers.firstOrNull()
                            ?: _state.value.currentLayers.first()
                    } else {
                        _state.value.currentActiveLayer
                    }

                _state.value = _state.value.copy(
                    currentLayers = updatedLayers,
                    currentActiveLayer = newActiveLayer
                )
            }
            is DrawingEvent.SelectLayer -> {
                val layer =
                    _state.value.currentLayers.firstOrNull { it.id == event.layer.id }
                        ?: return

                _state.value = _state.value.copy(
                    currentActiveLayer = layer
                )
            }

            is DrawingEvent.ToggleLayerVisibility -> {
                val updatedLayers =
                    _state.value.currentLayers.map { layer ->
                        if (layer.id == event.layer.id && layer is VectorLayer) {
                            layer.copy(isVisible = !layer.isVisible)
                        } else {
                            layer
                        }
                    }

                val newActiveLayer =
                    updatedLayers.firstOrNull { it.id == _state.value.currentActiveLayer.id }
                        ?: _state.value.currentActiveLayer

                _state.value = _state.value.copy(
                    currentLayers = updatedLayers,
                    currentActiveLayer = newActiveLayer
                )
            }



        }
    }


}