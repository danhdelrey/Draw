package com.example.draw.ui.feature.drawing.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.draw.data.model.base.DrawingPath

// 1. Hàm vẽ: Chỉ quan tâm việc vẽ 1 nét như thế nào
fun DrawScope.drawDrawingPath(drawingPath: DrawingPath) {
    val isEraser = false
    val blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
    val color = if (isEraser) Color.Transparent else Color(drawingPath.brush.colorArgb)

    if (drawingPath.points.size > 1) {
        val points = drawingPath.points
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)

                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val cur = points[i]

                    val midX = (prev.x + cur.x) / 2f
                    val midY = (prev.y + cur.y) / 2f

                    quadraticTo(
                        prev.x,
                        prev.y,
                        midX,
                        midY
                    )
                }
            }
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

// 2. Hàm tiện ích: Giữ nét vẽ không lọt ra ngoài mép giấy
fun Offset.clampToSize(size: Size): Offset {
    return Offset(
        x = this.x.coerceIn(0f, size.width),
        y = this.y.coerceIn(0f, size.height)
    )
}

