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

    // --- STACK UNDO / REDO ---
    // Sử dụng ArrayDeque để quản lý ngăn xếp hiệu quả
    private val undoStack = ArrayDeque<DrawingCommand>()
    private val redoStack = ArrayDeque<DrawingCommand>()

    fun onEvent(event: DrawingEvent) {
        when (event) {
            // --- NHÓM SỰ KIỆN VẼ (TOUCH) ---
            is DrawingEvent.StartDrawing -> {
                _state.value = _state.value.copy(
                    currentDrawingPath = DrawingPath(
                        points = listOf(event.currentTouchPosition),
                        brush = _state.value.currentBrush
                    ),
                    currentTouchPosition = event.currentTouchPosition
                )
            }

            is DrawingEvent.UpdateDrawing -> {
                val currentPath = _state.value.currentDrawingPath ?: return
                _state.value = _state.value.copy(
                    currentDrawingPath = currentPath.copy(
                        points = currentPath.points + event.currentTouchPosition
                    ),
                    currentTouchPosition = event.currentTouchPosition
                )
            }

            is DrawingEvent.EndDrawing -> {
                val currentPath = _state.value.currentDrawingPath ?: return
                val activeLayerId = _state.value.currentActiveLayer.id

                // 1. Tạo Command thêm nét vẽ
                val command = AddPathCommand(
                    layerId = activeLayerId,
                    path = currentPath
                )

                // 2. Thực thi Command (Lưu vào stack undo)
                performCommand(command)

                // 3. Dọn dẹp trạng thái vẽ tạm thời
                _state.value = _state.value.copy(
                    currentDrawingPath = null,
                    currentTouchPosition = null
                )
            }

            // --- NHÓM UNDO / REDO ---
            is DrawingEvent.Undo -> {
                if (undoStack.isNotEmpty()) {
                    val command = undoStack.removeLast() // Lấy lệnh mới nhất
                    val newState = command.undo(_state.value) // Hoàn tác
                    _state.value = newState

                    redoStack.addLast(command) // Đẩy sang redo stack
                    updateUndoRedoAvailability() // Cập nhật trạng thái nút bấm
                }
            }

            is DrawingEvent.Redo -> {
                if (redoStack.isNotEmpty()) {
                    val command = redoStack.removeLast() // Lấy lệnh đã undo
                    val newState = command.execute(_state.value) // Thực hiện lại
                    _state.value = newState

                    undoStack.addLast(command) // Đẩy lại vào undo stack
                    updateUndoRedoAvailability()
                }
            }

            // --- NHÓM LAYER ---
            is DrawingEvent.AddLayer -> {
                val newLayer = VectorLayer(id = Random.nextLong().toString())
                val command = AddLayerCommand(newLayer)
                performCommand(command)
            }

            is DrawingEvent.DeleteLayer -> {
                // Không cho xóa layer mặc định nếu logic yêu cầu
                if (event.layer.id == "default_layer") return

                // Tìm vị trí layer để sau này Undo chèn lại đúng chỗ
                val index = _state.value.currentLayers.indexOfFirst { it.id == event.layer.id }
                if (index != -1 && event.layer is VectorLayer) {
                    val command = DeleteLayerCommand(event.layer, index)
                    performCommand(command)
                }
            }

            is DrawingEvent.ToggleLayerVisibility -> {
                val command = ToggleLayerVisibilityCommand(event.layer.id)
                performCommand(command)
            }

            is DrawingEvent.SelectLayer -> {
                // Select layer chỉ thay đổi view, không thay đổi data ảnh
                // nên thường KHÔNG đưa vào Undo/Redo stack.
                val layer = _state.value.currentLayers.firstOrNull { it.id == event.layer.id }
                    ?: return
                _state.value = _state.value.copy(currentActiveLayer = layer)
            }

            // --- NHÓM KHÁC ---
            is DrawingEvent.ChangeBrush -> {
                _state.value = _state.value.copy(currentBrush = event.brush)
            }

            is DrawingEvent.SaveDrawing -> {
                screenModelScope.launch {
                    val name = "drawing_${Clock.System.now().toEpochMilliseconds()}"
                    val bytes = event.imageBitmap.toPngByteArray()
                    val result = imageRepository.saveImage(bytes, name)
                    if (result) println("Lưu thành công") else println("Lưu thất bại")
                }
            }
        }
    }

    /**
     * Hàm trung tâm để thực thi một DrawingCommand.
     * Logic:
     * 1. Thực hiện lệnh (lấy state mới).
     * 2. Cập nhật State Flow.
     * 3. Thêm lệnh vào Undo Stack.
     * 4. Xóa Redo Stack (vì lịch sử đã rẽ nhánh mới).
     */
    private fun performCommand(command: DrawingCommand) {
        val newState = command.execute(_state.value)
        _state.value = newState

        undoStack.addLast(command)
        redoStack.clear() // Quan trọng: Clear redo khi có hành động mới

        updateUndoRedoAvailability()
    }

    /**
     * Cập nhật trạng thái enable/disable cho nút Undo/Redo trên UI
     */
    private fun updateUndoRedoAvailability() {
         _state.value = _state.value.copy(
             canUndo = undoStack.isNotEmpty(),
             canRedo = redoStack.isNotEmpty()
         )
    }
}