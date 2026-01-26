package com.example.draw.data.model.serialization

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.base.PathMetadata
import com.example.draw.data.model.brush.AirBrush
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.BrushProperties
import com.example.draw.data.model.brush.BrushType
import com.example.draw.data.model.brush.BucketBrush
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.canvas.CanvasMetadata
import com.example.draw.data.model.canvas.DrawingCanvas
import com.example.draw.data.model.layer.BitmapLayer
import com.example.draw.data.model.layer.BlendMode
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.LayerMetadata
import com.example.draw.data.model.layer.LayerType
import com.example.draw.data.model.layer.VectorLayer

// ---------- To DTO ----------

fun DrawingCanvas.toDto(projectName: String): DrawingProject {
    return DrawingProject(
        id = this.id,
        name = projectName,
        width = this.width,
        height = this.height,
        backgroundColor = this.metadata.backgroundColor,
        layers = this.layers.map { it.toDto() },
        activeLayerId = this.activeLayerId,
        createdAt = this.metadata.createdAt,
        lastModified = this.metadata.modifiedAt,
        currentBrush = null // Can be passed explicitly if needed, but not part of canvas state
    )
}

fun Layer.toDto(): LayerData {
    return when (this) {
        is VectorLayer -> LayerData(
            id = id,
            name = name,
            type = "VECTOR",
            isVisible = isVisible,
            isLocked = isLocked,
            opacity = opacity,
            blendMode = blendMode.name,
            paths = paths.map { it.toDto() },
            bitmapContent = null,
            metadata = metadata.properties.mapValues { it.value.toString() }
        )
        is BitmapLayer -> LayerData(
            id = id,
            name = name,
            type = "BITMAP",
            isVisible = isVisible,
            isLocked = isLocked,
            opacity = opacity,
            blendMode = blendMode.name,
            paths = null,
            bitmapContent = bitmapId,
            metadata = metadata.properties.mapValues { it.value.toString() }
        )
    }
}

fun DrawingPath.toDto(): PathData {
    return PathData(
        id = id,
        points = points.map { PointData(it.x, it.y) },
        brush = brush.toDto(),
        metadata = metadata.properties.mapValues { it.value.toString() }
    )
}

fun Brush.toDto(): BrushData {
    return BrushData(
        id = id,
        type = type.name,
        size = size,
        opacity = opacity,
        colorArgb = colorArgb,
        properties = properties.properties.mapValues { it.value.toString() }
    )
}

// ---------- From DTO ----------

fun DrawingProject.toDomain(): DrawingCanvas {
    val domainLayers = layers.mapNotNull { it.toDomain() }
    return DrawingCanvas(
        id = id,
        width = width,
        height = height,
        layers = domainLayers,
        activeLayerId = activeLayerId,
        metadata = CanvasMetadata(
            title = name,
            createdAt = createdAt,
            modifiedAt = lastModified,
            backgroundColor = backgroundColor,
            properties = emptyMap()
        )
    )
}

fun LayerData.toDomain(): Layer? {
    val blendMode = try { BlendMode.valueOf(this.blendMode) } catch (e: Exception) { BlendMode.NORMAL }
    val metadata = LayerMetadata(
        type = if (type == "VECTOR") LayerType.VECTOR else LayerType.BITMAP,
        properties = this.metadata
    )

    return when (type) {
        "VECTOR" -> VectorLayer(
            id = id,
            name = name,
            isVisible = isVisible,
            isLocked = isLocked,
            opacity = opacity,
            blendMode = blendMode,
            metadata = metadata,
            paths = paths?.map { it.toDomain() } ?: emptyList()
        )
        "BITMAP" -> if (bitmapContent != null) {
            BitmapLayer(
                id = id,
                name = name,
                isVisible = isVisible,
                isLocked = isLocked,
                opacity = opacity,
                blendMode = blendMode,
                metadata = metadata,
                bitmapId = bitmapContent
            )
        } else null
        else -> null
    }
}

fun PathData.toDomain(): DrawingPath {
    val domainPoints = points.map { Offset(it.x, it.y) }
    val domainBrush = brush.toDomain()
    return DrawingPath(
        id = id,
        points = domainPoints,
        brush = domainBrush,
        metadata = PathMetadata(properties = metadata)
    )
}

fun BrushData.toDomain(): Brush {
    val typeEnum = try { BrushType.valueOf(type) } catch (_: Exception) { BrushType.SOLID }

    val brushProps = BrushProperties(
        properties.mapValues {
             // Try to convert to float if it looks like one, needed for properties like Density
             it.value.toFloatOrNull() ?: it.value
        }
    )

    val brush: Brush = when(typeEnum) {
        BrushType.SOLID -> SolidBrush.default()
        BrushType.AIR -> AirBrush.default()
        BrushType.ERASER -> EraserBrush.default()
        BrushType.BUCKET -> BucketBrush.default()
    }

    return brush
        .updateSize(size)
        .updateOpacity(opacity)
        .updateColor(colorArgb)
        .updateProperties(brushProps)
}
