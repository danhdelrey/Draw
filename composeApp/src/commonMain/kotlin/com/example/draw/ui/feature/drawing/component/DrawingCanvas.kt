package com.example.draw.ui.feature.drawing.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.AirBrush
import com.example.draw.data.model.brush.EraserBrush
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

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
    val brush = drawingPath.brush

    // Check brush type
    val isEraser = brush is EraserBrush
    val isAirBrush = brush is AirBrush

    val blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
    val color = if (isEraser) Color.Transparent else Color(brush.colorArgb)

    if (isAirBrush) {
        // AirBrush: Draw with spray-paint effect
        drawAirBrushPath(drawingPath, color)
    } else {
        // Regular brush or eraser: Draw smooth path
        drawSmoothPath(drawingPath, color, blendMode)
    }
}

/**
 * Draw a smooth path for regular brushes and eraser
 */
private fun DrawScope.drawSmoothPath(
    drawingPath: DrawingPath,
    color: Color,
    blendMode: BlendMode
) {
    val brush = drawingPath.brush
    val points = drawingPath.points

    if (points.size > 1) {
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
            alpha = brush.opacity,
            style = Stroke(
                width = brush.size,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            blendMode = blendMode
        )
    } else if (points.size == 1) {
        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = color,
            alpha = brush.opacity,
            strokeWidth = brush.size,
            cap = StrokeCap.Round,
            blendMode = blendMode
        )
    }
}

/**
 * Draw AirBrush with spray-paint particle effect
 */
private fun DrawScope.drawAirBrushPath(drawingPath: DrawingPath, color: Color) {
    val airBrush = drawingPath.brush as AirBrush
    val points = drawingPath.points
    val density = airBrush.density
    val brushSize = airBrush.size
    val opacity = airBrush.opacity

    // Calculate number of particles based on density
    val particlesPerPoint = (density * 20).toInt().coerceIn(5, 50)

    // Use path ID as seed for consistent particle positions on redraw
    val random = Random(drawingPath.id.hashCode())

    if (points.size == 1) {
        // Single point: draw spray at this point
        drawSprayParticles(
            center = points[0],
            radius = brushSize / 2f,
            particleCount = particlesPerPoint,
            color = color,
            opacity = opacity,
            random = random
        )
    } else if (points.size > 1) {
        // Multiple points: draw spray along the path
        for (i in 0 until points.size - 1) {
            val start = points[i]
            val end = points[i + 1]

            // Calculate distance between points
            val dx = end.x - start.x
            val dy = end.y - start.y
            val distance = sqrt(dx * dx + dy * dy)

            // Interpolate points along the segment
            val steps = (distance / (brushSize * 0.2f)).toInt().coerceAtLeast(1)

            for (step in 0..steps) {
                val t = step.toFloat() / steps
                val interpolatedX = start.x + dx * t
                val interpolatedY = start.y + dy * t
                val center = Offset(interpolatedX, interpolatedY)

                // Draw particles at this interpolated point
                drawSprayParticles(
                    center = center,
                    radius = brushSize / 2f,
                    particleCount = (particlesPerPoint * 0.5f).toInt().coerceAtLeast(2),
                    color = color,
                    opacity = opacity,
                    random = random
                )
            }
        }

        // Draw extra particles at the last point
        drawSprayParticles(
            center = points.last(),
            radius = brushSize / 2f,
            particleCount = particlesPerPoint,
            color = color,
            opacity = opacity,
            random = random
        )
    }
}

/**
 * Draw spray particles at a specific point
 */
private fun DrawScope.drawSprayParticles(
    center: Offset,
    radius: Float,
    particleCount: Int,
    color: Color,
    opacity: Float,
    random: Random
) {
    repeat(particleCount) {
        // Generate random position within circle using polar coordinates
        val angle = random.nextFloat() * 2 * PI.toFloat()
        val distance = sqrt(random.nextFloat()) * radius

        val x = center.x + cos(angle) * distance
        val y = center.y + sin(angle) * distance

        // Random particle size (smaller particles)
        val particleSize = random.nextFloat() * 2f + 1f

        // Random opacity variation for more natural look
        val particleOpacity = (opacity * (0.3f + random.nextFloat() * 0.7f)).coerceIn(0f, 1f)

        // Draw individual particle
        drawCircle(
            color = color,
            radius = particleSize,
            center = Offset(x, y),
            alpha = particleOpacity
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