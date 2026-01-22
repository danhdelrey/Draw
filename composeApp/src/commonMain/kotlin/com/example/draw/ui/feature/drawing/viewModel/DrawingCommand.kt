package com.example.draw.ui.feature.drawing.viewModel

import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.layer.VectorLayer

/**
 * Interface định nghĩa một lệnh có thể thực hiện (execute) và hoàn tác (undo).
 */
sealed interface DrawingCommand {
    fun execute(state: DrawingState): DrawingState
    fun undo(state: DrawingState): DrawingState
}

/**
 * Command 1: Thêm một nét vẽ (Add Path)
 * Dùng khi người dùng vẽ xong 1 nét (EndDrawing)
 */
data class AddPathCommand(
    val layerId: String,
    val path: DrawingPath
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        // Tìm layer theo ID và thêm path vào list paths của layer đó
        val updatedLayers = state.currentLayers.map { layer ->
            if (layer.id == layerId && layer is VectorLayer) {
                layer.copy(paths = layer.paths + path)
            } else {
                layer
            }
        }
        return state.copy(currentLayers = updatedLayers)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Tìm layer và xóa path vừa thêm (dựa trên reference của object path)
        val updatedLayers = state.currentLayers.map { layer ->
            if (layer.id == layerId && layer is VectorLayer) {
                layer.copy(paths = layer.paths - path)
            } else {
                layer
            }
        }
        return state.copy(currentLayers = updatedLayers)
    }
}

/**
 * Command 2: Thêm một Layer mới
 */
data class AddLayerCommand(
    val newLayer: VectorLayer
) : DrawingCommand {
    override fun execute(state: DrawingState): DrawingState {
        return state.copy(
            currentLayers = state.currentLayers + newLayer,
            currentActiveLayer = newLayer // Switch sang layer mới tạo
        )
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedLayers = state.currentLayers.filter { it.id != newLayer.id }

        // Nếu layer bị xóa đang active, phải switch active sang layer khác để tránh lỗi
        val nextActive = if (state.currentActiveLayer.id == newLayer.id) {
            updatedLayers.lastOrNull() ?: updatedLayers.firstOrNull() ?: state.currentActiveLayer
        } else {
            state.currentActiveLayer
        }

        return state.copy(
            currentLayers = updatedLayers,
            currentActiveLayer = nextActive
        )
    }
}

/**
 * Command 3: Xóa một Layer
 * Cần lưu lại index để khi Undo thì chèn lại đúng vị trí cũ
 */
data class DeleteLayerCommand(
    val layerToDelete: VectorLayer,
    val index: Int // Vị trí của layer trong danh sách
) : DrawingCommand {
    override fun execute(state: DrawingState): DrawingState {
        val updatedLayers = state.currentLayers.filter { it.id != layerToDelete.id }

        // Tính toán layer active mới
        val nextActive = if (state.currentActiveLayer.id == layerToDelete.id) {
            updatedLayers.getOrNull(index - 1) ?: updatedLayers.firstOrNull() ?: state.currentActiveLayer
            // Logic đơn giản: lấy thằng trước nó, hoặc thằng đầu tiên
        } else {
            state.currentActiveLayer
        }

        return state.copy(
            currentLayers = updatedLayers,
            currentActiveLayer = nextActive
        )
    }

    override fun undo(state: DrawingState): DrawingState {
        // Chèn lại layer vào đúng vị trí cũ
        val mutableLayers = state.currentLayers.toMutableList()
        if (index in 0..mutableLayers.size) {
            mutableLayers.add(index, layerToDelete)
        } else {
            mutableLayers.add(layerToDelete) // Fallback
        }

        return state.copy(
            currentLayers = mutableLayers,
            currentActiveLayer = layerToDelete // Khi undo xóa, thường user muốn focus lại nó
        )
    }
}

/**
 * Command 4: Ẩn/Hiện Layer
 */
data class ToggleLayerVisibilityCommand(
    val layerId: String
) : DrawingCommand {
    override fun execute(state: DrawingState): DrawingState {
        return toggleVisibility(state)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Toggle 2 lần thì về như cũ, nên logic giống hệt execute
        return toggleVisibility(state)
    }

    private fun toggleVisibility(state: DrawingState): DrawingState {
        val updatedLayers = state.currentLayers.map { layer ->
            if (layer.id == layerId && layer is VectorLayer) {
                layer.copy(isVisible = !layer.isVisible)
            } else {
                layer
            }
        }
        // Cập nhật lại active layer reference nếu cần (để UI update đúng)
        val newActive = updatedLayers.firstOrNull { it.id == state.currentActiveLayer.id }
            ?: state.currentActiveLayer

        return state.copy(currentLayers = updatedLayers, currentActiveLayer = newActive)
    }
}