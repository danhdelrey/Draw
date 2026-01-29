package com.example.draw.ui.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

data class CheckerboardTransform(
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero
)


@Composable
fun CheckerboardBackground(
    modifier: Modifier = Modifier,
    cellSize: Dp = 16.dp,
    lightColor: Color = Color(0xFFE0E0E0),
    darkColor: Color = Color(0xFFBDBDBD),
    alpha: Float = 1f,
    transform: CheckerboardTransform? = null
) {
    val density = LocalDensity.current
    val cellPx = with(density) { cellSize.toPx() }

    Canvas(modifier = modifier) {
        if (cellPx <= 0f) return@Canvas

        val scale = transform?.scale ?: 1f
        val offset = transform?.offset ?: Offset.Zero

        val scaledCell = cellPx * scale

        if (scaledCell <= 0f) return@Canvas

        // Visible area in "world space"
        val startX = floor((-offset.x) / scaledCell) * scaledCell
        val startY = floor((-offset.y) / scaledCell) * scaledCell

        val endX = size.width - offset.x
        val endY = size.height - offset.y

        val light = lightColor.copy(alpha = alpha)
        val dark = darkColor.copy(alpha = alpha)

        var y = startY
        while (y < endY) {
            var x = startX
            while (x < endX) {
                val isEven =
                    ((floor(x / scaledCell) + floor(y / scaledCell)).toInt() % 2 == 0)

                drawRect(
                    color = if (isEven) light else dark,
                    topLeft = Offset(
                        x = x + offset.x,
                        y = y + offset.y
                    ),
                    size = Size(scaledCell, scaledCell)
                )
                x += scaledCell
            }
            y += scaledCell
        }
    }
}
