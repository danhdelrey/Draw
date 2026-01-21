package com.example.draw.ui.feature.drawing.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.ui.feature.drawing.utils.drawDrawingPath

@Composable
fun DrawingCanvas(
    paths: List<DrawingPath>,
    currentPath: DrawingPath?,
    isEraserMode: Boolean,
    currentTouchPosition: Offset?,
    brushSize: Float,
    modifier: Modifier = Modifier
) {
    val graphicsLayer = rememberGraphicsLayer()
    // Canvas thực sự
    Canvas(
        modifier = modifier
            // Logic để cục tẩy hoạt động đúng (Layer rendering)
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
    ) {
        // 1. Vẽ các nét cũ
        paths.forEach { path ->
            drawDrawingPath(path)
        }

        // 2. Vẽ nét đang kéo
        currentPath?.let { path ->
            drawDrawingPath(path)
        }

        // 3. Vẽ hình tròn cục tẩy (Indicator)
        if (isEraserMode && currentTouchPosition != null) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.5f),
                radius = brushSize / 2f,
                center = currentTouchPosition,
                style = Stroke(width = 2f)
            )
        }
    }
}