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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.layer.VectorLayer

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
            .background(Color.Gray) // Màu nền khung chứa (chỉ thấy nếu nền giấy trắng bị ẩn)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        // Wrapper để đảm bảo tỷ lệ khung hình
        Box(
            modifier = Modifier
                .aspectRatio(canvasAspectRatio)
                .fillMaxSize()
        ) {
            // LỚP 1: Nền giấy trắng (Vẽ riêng biệt dưới đáy)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )

            // LỚP 2: Nội dung nét vẽ (Vẽ đè lên trên)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    // QUAN TRỌNG: Cô lập layer này.
                    // Khi tẩy (BlendMode.Clear), nó sẽ trở nên trong suốt -> Lộ ra LỚP 1 (Màu trắng)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                val scaleFactor = size.width / originalWidth

                scale(scale = scaleFactor, pivot = Offset.Zero) {
                    // Không vẽ drawRect(Color.White) ở đây nữa!

                    layer.paths.forEach { drawingPath ->
                        val isEraser = drawingPath.brush is EraserBrush
                        val blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
                        val color = if (isEraser) Color.Transparent else Color(drawingPath.brush.colorArgb)

                        val path = Path()
                        if (drawingPath.points.size > 1) {
                            path.moveTo(drawingPath.points[0].x, drawingPath.points[0].y)
                            for (i in 1 until drawingPath.points.size) {
                                path.lineTo(drawingPath.points[i].x, drawingPath.points[i].y)
                            }
                            drawPath(
                                path = path,
                                color = color,
                                alpha = drawingPath.brush.opacity,
                                style = Stroke(
                                    width = drawingPath.brush.size,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                ),
                                blendMode = blendMode
                            )
                        } else if (drawingPath.points.size == 1) {
                            drawPoints(
                                points = drawingPath.points,
                                pointMode = PointMode.Points,
                                color = color,
                                alpha = drawingPath.brush.opacity,
                                strokeWidth = drawingPath.brush.size,
                                cap = StrokeCap.Round,
                                blendMode = blendMode
                            )
                        }
                    }
                }
            }
        }
    }
}