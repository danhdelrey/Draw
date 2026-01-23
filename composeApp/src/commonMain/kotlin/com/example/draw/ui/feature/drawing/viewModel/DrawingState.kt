package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.canvas.DrawingCanvas
import com.example.draw.data.model.layer.VectorLayer

/**
 * Drawing state using the new refactored models.
 *
 * Design improvements:
 * - Uses DrawingCanvas for centralized canvas state
 * - Leverages factory methods from models
 * - More explicit state structure
 */
data class DrawingState(
    // Canvas state (centralized)
    val canvas: DrawingCanvas = createDefaultCanvas(),

    // Drawing tool state
    val currentBrush: Brush = SolidBrush.default(),

    // Ephemeral drawing state (not persisted)
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,

    // Undo/Redo state
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
) {
    // Convenience accessors
    val activeLayer: VectorLayer?
        get() = canvas.activeLayer as? VectorLayer

    val layers: List<com.example.draw.data.model.layer.Layer>
        get() = canvas.layers

    companion object {
        /**
         * Create default canvas with initial layer
         */
        private fun createDefaultCanvas(): DrawingCanvas {
            val defaultLayer = VectorLayer.create(
                id = "default_layer",
                name = "Layer 1"
            )
            return DrawingCanvas.create(
                width = CanvasConfig.DEFAULT_WIDTH,
                height = CanvasConfig.DEFAULT_HEIGHT,
                initialLayer = defaultLayer
            )
        }
    }
}
