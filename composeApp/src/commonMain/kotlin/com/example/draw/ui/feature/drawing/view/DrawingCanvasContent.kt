package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import com.example.draw.ui.feature.drawing.view.gesture.CanvasGestureWrapper
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState

@Composable
fun DrawingCanvasContent(
    state: DrawingState,
    viewModel: DrawingScreenViewModel,
    rootGraphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current

        // Calculate scale to fit canvas inside screen (contain behavior)
        val canvasWidth = state.canvas.width.toFloat()
        val canvasHeight = state.canvas.height.toFloat()

        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        val scale = minOf(
            availableWidth / canvasWidth,
            availableHeight / canvasHeight
        )

        // Scaling factors
        val inputScale = 1f / scale

        val displayedWidth = canvasWidth * scale
        val displayedHeight = canvasHeight * scale

        val pivot = state.layerTransformPivot
        val layerTransformOrigin = if (pivot != null && canvasWidth != 0f && canvasHeight != 0f) {
            TransformOrigin(pivot.x / canvasWidth, pivot.y / canvasHeight)
        } else {
            TransformOrigin(0.5f, 0.5f)
        }

        // Calculate canvas offset from top-left of the container (Alignment.Center places it here)
        val layoutOffsetX = (availableWidth - displayedWidth) / 2f
        val layoutOffsetY = (availableHeight - displayedHeight) / 2f

        // --- TRANSFORMATION STATE ---
        var zoom by remember { mutableStateOf(1f) }
        var angle by remember { mutableStateOf(0f) }
        var translation by remember { mutableStateOf(Offset.Zero) }

        CanvasGestureWrapper(
            state = state,
            viewModel = viewModel,
            renderScale = scale,
            layoutOffsetX = layoutOffsetX,
            layoutOffsetY = layoutOffsetY,
            zoom = zoom,
            angle = angle,
            translation = translation,
            onViewTransformChange = { newZoom, newAngle, newTranslation ->
                zoom = newZoom
                angle = newAngle
                translation = newTranslation
            }
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = with(density) { displayedWidth.toDp() },
                        height = with(density) { displayedHeight.toDp() }
                    )
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = translation.x
                        translationY = translation.y
                        rotationZ = angle
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .background(Color.White)
                    .drawWithContent {
                        rootGraphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(rootGraphicsLayer)
                    }
            ) {
                CanvasLayerList(
                    state = state,
                    viewModel = viewModel,
                    renderScale = scale,
                    inputScale = inputScale,
                    layerTransformOrigin = layerTransformOrigin
                )

                CanvasOverlays(
                    state = state,
                    viewModel = viewModel,
                    renderScale = scale,
                    inputScale = inputScale,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    layerTransformOrigin = layerTransformOrigin
                )
            }
        }
    }
}
