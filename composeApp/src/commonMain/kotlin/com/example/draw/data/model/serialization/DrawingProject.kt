package com.example.draw.data.model.serialization

import com.example.draw.data.model.util.currentTimeMillis
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for saving/loading a drawing project.
 * This effectively snapshots the entire DrawingCanvas and relevant state.
 */
@Serializable
data class DrawingProject(
    val id: String, //path to file + file name
    val name: String, // file name
    val width: Float,
    val height: Float,
    val backgroundColor: Long = 0xFFFFFFFF,
    val layers: List<LayerData>,
    val activeLayerId: String,
    val createdAt: Long,
    val lastModified: Long,
    val currentBrush: BrushData? = null
){
    companion object{
        fun defaultProject(): DrawingProject{
            return DrawingProject(
                id = "untitled_project",
                name = "untitled_project",
                width = 1920f,
                height = 1080f,
                backgroundColor = 0xFFFFFFFF,
                layers = listOf(
                    LayerData(
                        id = "layer_1",
                        name = "Layer 1",
                        type = "VECTOR",
                        isVisible = true,
                        isLocked = false,
                        opacity = 1.0f,
                        blendMode = "NORMAL",
                        paths = emptyList(),
                        bitmapContent = null,
                        metadata = emptyMap()
                    )
                ),
                activeLayerId = "layer_1",
                createdAt = currentTimeMillis(),
                lastModified = currentTimeMillis(),
                currentBrush = null
            )
        }
    }
}

/**
 * DTO for a Layer.
 * Represents both Vector and Bitmap layers.
 */
@Serializable
data class LayerData(
    val id: String,
    val name: String,
    val type: String, // "VECTOR" or "BITMAP"
    val isVisible: Boolean,
    val isLocked: Boolean,
    val opacity: Float,
    val blendMode: String, // "NORMAL", "MULTIPLY", etc.

    // For VectorLayer
    val paths: List<PathData>? = null,

    // For BitmapLayer
    // In a real app, this might be a path to a separate image file or Base64 string
    val bitmapContent: String? = null,

    // Simplified metadata (converted to strings for easier serialization)
    val metadata: Map<String, String> = emptyMap()
)

/**
 * DTO for a DrawingPath.
 */
@Serializable
data class PathData(
    val id: String,
    val points: List<PointData>,
    val brush: BrushData,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * DTO for a point (replacing Offset which is not serializable).
 */
@Serializable
data class PointData(
    val x: Float,
    val y: Float
)

/**
 * DTO for Brush configuration.
 */
@Serializable
data class BrushData(
    val id: String,
    val type: String, // "SOLID", "AIR", etc.
    val size: Float,
    val opacity: Float,
    val colorArgb: Long,
    // Properties stored as strings for simplicity
    val properties: Map<String, String> = emptyMap()
)

