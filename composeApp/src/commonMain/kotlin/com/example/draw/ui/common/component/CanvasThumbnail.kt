package com.example.draw.ui.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.feature.drawing.component.drawDrawingPath

@Composable
fun CanvasThumbnail(
    layers: List<VectorLayer>,
    canvasWidth: Float,
    canvasHeight: Float,
    backgroundColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current

        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        // Determine scale to fit the canvas within the constraints (Contain strategy)
        // If constraints are unbounded (e.g., Infinity), we default to scale 1 (or handle appropriately)
        val widthScale = if (availableWidth.isFinite()) availableWidth / canvasWidth else 1f
        val heightScale = if (availableHeight.isFinite()) availableHeight / canvasHeight else 1f

        val scale = minOf(widthScale, heightScale)

        val displayedWidth = canvasWidth * scale
        val displayedHeight = canvasHeight * scale

        Box(
            modifier = Modifier
                .size(
                    width = with(density) { displayedWidth.toDp() },
                    height = with(density) { displayedHeight.toDp() }
                )
                .background(backgroundColor)
        ) {
            layers.forEach { layer ->
                if (layer.isVisible) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = layer.opacity
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                    ) {
                        // All drawing must be scaled relative to the displayed size
                        // Since the Canvas is sized to match displayedWidth/Height,
                        // we scale the drawing context to match the original canvas coordinates.
                        scale(scale = scale, pivot = Offset.Zero) {
                            layer.paths.forEach { path ->
                                drawDrawingPath(path)
                            }
                        }
                    }
                }
            }
        }
    }
}

