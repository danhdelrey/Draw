package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.shape.EllipseState
import com.example.draw.data.model.shape.RectangleState
import com.example.draw.data.model.canvas.CanvasMetadata
import com.example.draw.data.model.canvas.DrawingCanvas
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.model.serialization.toDomain
import com.example.draw.data.model.serialization.toDto
import com.example.draw.data.model.transform.LayerTransformState

/**
 * Drawing state using the new refactored models.
 *
 * Design improvements:
 * - Uses DrawingCanvas for centralized canvas state
 * - Leverages factory methods from models
 * - More explicit state structure
 */
data class DrawingState(
    // Project metadata
    val projectName: String = "Untitled Project",

    // Canvas state (centralized)
    val canvas: DrawingCanvas = createDefaultCanvas(),

    // Drawing tool state
    val currentBrush: Brush = SolidBrush.default(),

    // Ephemeral drawing state (not persisted)
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,

    // Undo/Redo state
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,

    // Ellipse Drawing Mode state
    val ellipseMode: EllipseState? = null,

    // Rectangle Drawing Mode state
    val rectangleMode: RectangleState? = null,

    //for UX purposes
    val isUserDrawing: Boolean = false,
    val isInLayerTransformationMode: Boolean = false,
    val transformLayerId: String? = null,
    val layerTransformState: LayerTransformState = LayerTransformState(),
    val layerTransformPivot: Offset? = null
) {
    // Convenience accessors

    val layers: List<com.example.draw.data.model.layer.Layer>
        get() = canvas.layers

    fun toDrawingProject(
        projectName: String = this.projectName
    ): DrawingProject{
        return DrawingProject(
            id = canvas.id,
            name = projectName,
            width = canvas.width,
            height = canvas.height,
            backgroundColor = canvas.metadata.backgroundColor,
            layers = canvas.layers.map { it.toDto() },
            activeLayerId = canvas.activeLayerId,
            createdAt = canvas.metadata.createdAt,
            lastModified = canvas.metadata.modifiedAt,
            currentBrush = currentBrush.toDto()
        )
    }



    companion object {
        /**
         * Create default canvas with initial layer
         */
        private fun createDefaultCanvas(): DrawingCanvas {
            val layer1 = VectorLayer.create(
                id = "default_layer",
                name = "Layer 1"
            ).copy(isLocked = true) // First layer is locked

            val layer2 = VectorLayer.create(
                id = "default_layer_2",
                name = "Layer 2"
            )

            // Start with layer 1, then add layer 2 and set it as active
            return DrawingCanvas.create(
                width = CanvasConfig.DEFAULT_WIDTH,
                height = CanvasConfig.DEFAULT_HEIGHT,
                initialLayer = layer1
            ).addLayer(layer2)
             .setActiveLayer(layer2.id)
        }

        fun fromDrawingProject(project: DrawingProject): DrawingState {
            val canvas = DrawingCanvas(
                id = project.id,
                width = project.width,
                height = project.height,
                layers = project.layers.map { it.toDomain() },
                activeLayerId = project.activeLayerId,
                metadata = CanvasMetadata(
                    backgroundColor = project.backgroundColor,
                    createdAt = project.createdAt,
                    modifiedAt = project.lastModified
                )
            )
            return DrawingState(
                projectName = project.name,
                canvas = canvas,
                currentBrush = project.currentBrush?.toDomain() ?: SolidBrush.default()
            )
        }
    }
}
