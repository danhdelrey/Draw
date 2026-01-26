package com.example.draw.data.model.serialization

/**
 * Data Transfer Object (DTO) for saving/loading a drawing project.
 * This effectively snapshots the entire DrawingCanvas and relevant state.
 */
data class DrawingProject(
    val id: String,
    val name: String,
    val width: Float,
    val height: Float,
    val backgroundColor: Long = 0xFFFFFFFF,
    val layers: List<LayerData>,
    val activeLayerId: String,
    val createdAt: Long,
    val lastModified: Long,
    val currentBrush: BrushData? = null
)

/**
 * DTO for a Layer.
 * Represents both Vector and Bitmap layers.
 */
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
data class PathData(
    val id: String,
    val points: List<PointData>,
    val brush: BrushData,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * DTO for a point (replacing Offset which is not serializable).
 */
data class PointData(
    val x: Float,
    val y: Float
)

/**
 * DTO for Brush configuration.
 */
data class BrushData(
    val id: String,
    val type: String, // "SOLID", "AIR", etc.
    val size: Float,
    val opacity: Float,
    val colorArgb: Long,
    // Properties stored as strings for simplicity
    val properties: Map<String, String> = emptyMap()
)

