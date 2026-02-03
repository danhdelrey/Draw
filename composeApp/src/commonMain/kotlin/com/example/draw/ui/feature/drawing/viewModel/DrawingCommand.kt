package com.example.draw.ui.feature.drawing.viewModel

import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.layer.VectorLayer

/**
 * Interface defining commands that can be executed and undone.
 *
 * Design improvements:
 * - Uses DrawingCanvas update methods
 * - Cleaner separation of concerns
 * - More maintainable code
 */
sealed interface DrawingCommand {
    fun execute(state: DrawingState): DrawingState
    fun undo(state: DrawingState): DrawingState
}

/**
 * Command 1: Add a drawing path to a layer
 * Used when user finishes drawing a stroke (EndDrawing)
 */
data class AddPathCommand(
    val layerId: String,
    val path: DrawingPath
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                layer.addPath(path)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                layer.removePath(path)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 2: Add a new layer
 */
data class AddLayerCommand(
    val newLayer: VectorLayer,
    val position: Int? = null
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas
            .addLayer(newLayer, position)
            .setActiveLayer(newLayer.id)
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.removeLayer(newLayer.id)
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 3: Delete a layer
 * Stores the index to restore at the correct position on undo
 */
data class DeleteLayerCommand(
    val layerToDelete: VectorLayer,
    val index: Int
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.removeLayer(layerToDelete.id)
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas
            .addLayer(layerToDelete, index)
            .setActiveLayer(layerToDelete.id)
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 4: Toggle layer visibility
 */
data class ToggleLayerVisibilityCommand(
    val layerId: String
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            layer.updateVisibility(!layer.isVisible)
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Toggle back
        return execute(state)
    }
}

/**
 * Command 5: Update layer opacity
 */
data class UpdateLayerOpacityCommand(
    val layerId: String,
    val oldOpacity: Float,
    val newOpacity: Float
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            layer.updateOpacity(newOpacity)
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            layer.updateOpacity(oldOpacity)
        }
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 6: Rename layer
 */
data class RenameLayerCommand(
    val layerId: String,
    val oldName: String,
    val newName: String
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            layer.updateName(newName)
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            layer.updateName(oldName)
        }
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 7: Reorder layer
 */
data class ReorderLayerCommand(
    val fromIndex: Int,
    val toIndex: Int
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        // DrawingCanvas moveLayer handles 0-based indexing appropriately
        // We defer to domain logic in DrawingCanvas
        val updatedCanvas = state.canvas.moveLayer(fromIndex, toIndex)
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Reverse operation: move from toIndex back to fromIndex
        val updatedCanvas = state.canvas.moveLayer(toIndex, fromIndex)
        return state.copy(canvas = updatedCanvas)
    }
}

/**
 * Command 8: Invert layer colors
 */
data class InvertLayerCommand(
    val layerId: String
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                val newPaths = layer.paths.map { path ->
                    val originalColor = path.brush.colorArgb
                    val alpha = (originalColor shr 24) and 0xFF
                    val red = (originalColor shr 16) and 0xFF
                    val green = (originalColor shr 8) and 0xFF
                    val blue = originalColor and 0xFF

                    val newRed = 255 - red
                    val newGreen = 255 - green
                    val newBlue = 255 - blue

                    val newColor = (alpha shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    val newBrush = path.brush.updateColor(newColor)
                    path.updateBrush(newBrush)
                }
                layer.updatePaths(newPaths)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Invert again to restore
        return execute(state)
    }
}
