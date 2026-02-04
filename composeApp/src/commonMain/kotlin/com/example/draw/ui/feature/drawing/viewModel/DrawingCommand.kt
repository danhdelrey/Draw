package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.transform.LayerTransformState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

/**
 * Command 9: Flip layer horizontal
 */
data class FlipLayerHorizontalCommand(
    val layerId: String
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val canvasWidth = state.canvas.width
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                val newPaths = layer.paths.map { path ->
                    val newPoints = path.points.map { point ->
                        point.copy(x = canvasWidth - point.x)
                    }
                    path.copy(points = newPoints)
                }
                layer.updatePaths(newPaths)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Flip again to restore
        return execute(state)
    }
}

/**
 * Command 10: Flip layer vertical
 */
data class FlipLayerVerticalCommand(
    val layerId: String
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val canvasHeight = state.canvas.height
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                val newPaths = layer.paths.map { path ->
                    val newPoints = path.points.map { point ->
                        point.copy(y = canvasHeight - point.y)
                    }
                    path.copy(points = newPoints)
                }
                layer.updatePaths(newPaths)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        // Flip again to restore
        return execute(state)
    }
}

/**
 * Command 11: Apply a transform to a layer's vector paths
 */
data class TransformLayerCommand(
    val layerId: String,
    val originalPaths: List<DrawingPath>,
    val transform: LayerTransformState,
    val pivot: Offset
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                val transformedPaths = applyTransform(originalPaths, transform, pivot)
                layer.updatePaths(transformedPaths)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    override fun undo(state: DrawingState): DrawingState {
        val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
            if (layer is VectorLayer) {
                layer.updatePaths(originalPaths)
            } else {
                layer
            }
        }
        return state.copy(canvas = updatedCanvas)
    }

    private fun applyTransform(
        paths: List<DrawingPath>,
        transform: LayerTransformState,
        pivot: Offset
    ): List<DrawingPath> {
        val rotationRad = transform.rotation * PI / 180.0
        val cosVal = cos(rotationRad)
        val sinVal = sin(rotationRad)

        return paths.map { path ->
            val newPoints = path.points.map { point ->
                val relative = point - pivot
                val scaled = relative * transform.scale
                val rotated = Offset(
                    (scaled.x * cosVal - scaled.y * sinVal).toFloat(),
                    (scaled.x * sinVal + scaled.y * cosVal).toFloat()
                )
                rotated + pivot + transform.translation
            }
            val scaledBrush = scaleBrush(path.brush, transform.scale)
            path.copy(points = newPoints, brush = scaledBrush)
        }
    }

    private fun scaleBrush(brush: Brush, scale: Float): Brush {
        return brush.updateSize(brush.size * scale)
    }
}

/**
 * Command 12: Merge layers
 */
data class MergeLayerCommand(
    val fromLayer: VectorLayer,
    val toLayer: VectorLayer,
    val fromIndex: Int,
    val toIndex: Int
) : DrawingCommand {

    override fun execute(state: DrawingState): DrawingState {
        val mergedPaths = if (toIndex < fromIndex) {
            toLayer.paths + fromLayer.paths // Target is below Source
        } else {
            fromLayer.paths + toLayer.paths // Target is above Source
        }

        val newTargetLayer = toLayer.copy(paths = mergedPaths)
        val currentLayers = state.layers.toMutableList()

        if (fromIndex in currentLayers.indices && toIndex in currentLayers.indices) {
            currentLayers[toIndex] = newTargetLayer
            currentLayers.removeAt(fromIndex)
        }

        return state.copy(
            canvas = state.canvas
                .updateLayers(currentLayers)
                .setActiveLayer(newTargetLayer.id)
        )
    }

    override fun undo(state: DrawingState): DrawingState {
        val currentLayers = state.layers.toMutableList()

        // Reconstruct the original state
        if (fromIndex >= 0 && fromIndex <= currentLayers.size) {
             currentLayers.add(fromIndex, fromLayer)
        }

        if (toIndex in currentLayers.indices) {
            currentLayers[toIndex] = toLayer
        }

        return state.copy(
            canvas = state.canvas
                .updateLayers(currentLayers)
                .setActiveLayer(toLayer.id)
        )
    }
}
