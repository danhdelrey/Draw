package com.example.draw.ui.common.canvasOverlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import kotlin.collections.ifEmpty

@Composable
fun RectBoundOverlay(
    drawingPaths: List<DrawingPath>,
    shouldShowRectOverlay: Boolean = false,
    canvasWidth: Float,
    canvasHeight: Float,
    sX: Float = 1f,
    sY: Float = 1f,
    rZ: Float = 0f,
    transX: Float = 0f,
    transY: Float = 0f,
    transform: TransformOrigin = TransformOrigin.Center,
    layerTransformPivot: Offset? = null,
    onUpdateTransformPivot: (Offset) -> Unit = {},
    onButtonClicked: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        val density = LocalDensity.current

        // Calculate scale to fit canvas inside screen (contain behavior)
        val canvasWidth = canvasWidth
        val canvasHeight = canvasHeight

        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        val scale = minOf(
            availableWidth / canvasWidth,
            availableHeight / canvasHeight
        )

        val layerPoints = drawingPaths
            .flatMap { it.points }
            .ifEmpty { null }

        val boundsCanvas = layerPoints
            ?.let { points ->
                val minX = points.minOf { it.x }
                val maxX = points.maxOf { it.x }
                val minY = points.minOf { it.y }
                val maxY = points.maxOf { it.y }

                val maxStrokeRadius = drawingPaths
                    .maxOfOrNull { it.brush.size / 2f }
                    ?: 0f

                Rect(
                    minX - maxStrokeRadius,
                    minY - maxStrokeRadius,
                    maxX + maxStrokeRadius,
                    maxY + maxStrokeRadius
                )
            }
        LaunchedEffect(shouldShowRectOverlay, boundsCanvas) {
            if (shouldShowRectOverlay && layerTransformPivot == null && boundsCanvas != null) {
                val center = boundsCanvas.center
                onUpdateTransformPivot(center)
            }
        }

        if (shouldShowRectOverlay && boundsCanvas != null) {
            val boundsScreen = Rect(
                left = boundsCanvas.left * scale,
                top = boundsCanvas.top * scale,
                right = boundsCanvas.right * scale,
                bottom = boundsCanvas.bottom * scale
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = sX
                        scaleY = sY
                        rotationZ = rZ
                        translationX = transX * scale
                        translationY = transY * scale
                        transformOrigin = transform
                    }
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRect(
                        color = Color.Blue,
                        topLeft = boundsScreen.topLeft,
                        size = boundsScreen.size,
                        style = Stroke(width = 8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(
                            x = with(density) { boundsScreen.right.toDp() - 20.dp },
                            y = with(density) { boundsScreen.top.toDp() - 20.dp }
                        )
                        .size(40.dp)
                        .background(Color.Red)
                        .clickable { onButtonClicked() }
                )
            }
        }
    }

}