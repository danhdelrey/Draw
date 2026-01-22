package com.example.draw.ui.feature.drawing.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.EraserBrush

@Composable
fun DrawingCanvas(
    paths: List<DrawingPath>,
    currentPath: DrawingPath?,
    isEraserMode: Boolean,
    currentTouchPosition: Offset?,
    brushSize: Float,
    renderScale: Float, // <--- THÊM THAM SỐ NÀY
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        // Áp dụng scale toàn bộ nội dung vẽ từ gốc (0,0)
        // Logic: Tọa độ path là 1080p -> Scale xuống khớp màn hình
        scale(scale = renderScale, pivot = Offset.Zero) {

            // 1. Vẽ các nét cũ
            paths.forEach { path ->
                drawDrawingPath(path)
            }

            // 2. Vẽ nét đang kéo
            currentPath?.let { path ->
                drawDrawingPath(path)
            }
        }

        // 3. Vẽ hình tròn cục tẩy (Indicator)
        // Riêng cái này vẽ theo tọa độ ngón tay (đã là màn hình) nên KHÔNG nằm trong block scale ở trên
        // Hoặc nếu currentTouchPosition là tọa độ gốc, thì đưa vào trong.
        // Nhưng ở ViewModel bạn đang lưu currentTouchPosition là tọa độ input (màn hình) hay gốc?
        // Theo code trước: ViewModel lưu currentTouchPosition là tọa độ GỐC (inputScale).
        // NÊN ta cũng vẽ nó trong khối scale để nó to nhỏ đúng tỷ lệ.

        if (isEraserMode && currentTouchPosition != null) {
            scale(scale = renderScale, pivot = Offset.Zero) {
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.5f),
                    radius = brushSize / 2f,
                    center = currentTouchPosition,
                    style = Stroke(width = 2f / renderScale) // Giữ nét viền mảnh
                )
            }
        }
    }
}


// 1. Hàm vẽ: Chỉ quan tâm việc vẽ 1 nét như thế nào
fun DrawScope.drawDrawingPath(drawingPath: DrawingPath) {
    val isEraser = drawingPath.brush::class == EraserBrush::class
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

fun Modifier.drawingInput(
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
): Modifier {
    return this.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                // Tự động kẹp tọa độ vào trong size của canvas
                onDragStart(offset.clampToSize(size.toSize()))
            },
            onDrag = { change, _ ->
                // Tự động kẹp tọa độ
                onDrag(change.position.clampToSize(size.toSize()))
            },
            onDragEnd = { onDragEnd() }
        )
    }
}