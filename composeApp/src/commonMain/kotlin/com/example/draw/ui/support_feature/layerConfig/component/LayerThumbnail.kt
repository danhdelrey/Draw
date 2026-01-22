package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.layer.VectorLayer

@Composable
fun LayerThumbnail(
    layer: VectorLayer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // 1. Tính tỉ lệ thu nhỏ
        // Lấy chiều rộng hiện tại của ô thumbnail chia cho chiều rộng gốc của canvas
        val scaleX = size.width / CanvasConfig.FIXED_WIDTH
        val scaleY = size.height / CanvasConfig.FIXED_HEIGHT

        // Thông thường ta muốn giữ đúng tỉ lệ khung hình (không bị méo),
        // ta chọn scale nhỏ nhất trong 2 chiều (Fit Center)
        // Hoặc nếu Box bên ngoài đã set đúng aspectRatio thì scaleX sẽ bằng scaleY.
        val scaleFactor = minOf(scaleX, scaleY)

        // 2. Dịch chuyển (Optional): Để hình nằm giữa nếu tỉ lệ khung không khớp
        // (Nếu bạn set aspectRatio chuẩn ở Bước 3 thì không cần dòng translate này)
        // translate(left = ..., top = ...)

        // 3. Thực hiện vẽ với tỉ lệ đã tính
        scale(scale = scaleFactor, pivot = Offset.Zero) {

            // Vẽ nền trắng cho thumbnail (giống tờ giấy)
            drawRect(
                color = Color.White,
                size = Size(CanvasConfig.FIXED_WIDTH, CanvasConfig.FIXED_HEIGHT)
            )

            // Vẽ các nét
            layer.paths.forEach { drawingPath ->
                // ... Logic vẽ Path giữ nguyên như câu trả lời trước ...
                // Code vẽ path ở đây (copy từ đoạn trước)
                val path = Path()
                if (drawingPath.points.size > 1) {
                    path.moveTo(drawingPath.points[0].x, drawingPath.points[0].y)
                    for (i in 1 until drawingPath.points.size) {
                        path.lineTo(drawingPath.points[i].x, drawingPath.points[i].y)
                    }
                    drawPath(
                        path = path,
                        color = Color(drawingPath.brush.colorArgb),
                        alpha = drawingPath.brush.opacity,
                        style = Stroke(
                            width = drawingPath.brush.size,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                } else if (drawingPath.points.size == 1) {
                    drawPoints(
                        points = drawingPath.points,
                        pointMode = PointMode.Points,
                        color = Color(drawingPath.brush.colorArgb),
                        alpha = drawingPath.brush.opacity,
                        strokeWidth = drawingPath.brush.size,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}