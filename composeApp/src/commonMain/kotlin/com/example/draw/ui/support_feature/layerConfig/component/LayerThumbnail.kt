package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.drawDrawingPath

/**
 * Thumbnail preview for a vector layer.
 * Uses unified drawing logic from DrawingCanvas to support all brush types including AirBrush.
 */
@Composable
fun LayerThumbnail(
    layer: VectorLayer,
    modifier: Modifier = Modifier
) {
    val originalWidth = CanvasConfig.DEFAULT_WIDTH
    val originalHeight = CanvasConfig.DEFAULT_HEIGHT
    val canvasAspectRatio = originalWidth / originalHeight

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray) // Container background
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        // Maintain aspect ratio
        Box(
            modifier = Modifier
                .aspectRatio(canvasAspectRatio)
                .fillMaxSize()
        ) {
            // Layer 1: White paper background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )

            // Layer 2: Drawing content
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    // Important: Isolate this layer for BlendMode.Clear to work properly
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                val scaleFactor = size.width / originalWidth

                scale(scale = scaleFactor, pivot = Offset.Zero) {
                    // Draw all paths using unified drawing logic
                    layer.paths.forEach { drawingPath ->
                        drawDrawingPath(drawingPath)
                    }
                }
            }
        }
    }
}

