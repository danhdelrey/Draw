package com.example.draw.ui.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.AirBrush
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.component.drawDrawingPath

/**
 * Preview component that shows a wavy line drawn with the current brush.
 * Supports all brush types including AirBrush with spray effect.
 */
@Composable
fun WavyLinePreview(brush: Brush = SolidBrush.default()) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
    ) {
        val width = size.width
        val height = size.height

        // Generate wavy path points
        val points = generateWavyPathPoints(width, height)

        // Create DrawingPath with current brush
        val drawingPath = DrawingPath(
            points = points,
            brush = brush
        )

        // Use unified drawing logic from DrawingCanvas
        drawDrawingPath(drawingPath)
    }
}

/**
 * Generate points for a wavy preview path
 */
private fun generateWavyPathPoints(width: Float, height: Float): List<Offset> {
    val points = mutableListOf<Offset>()
    val segments = 50 // Number of points for smooth curve

    for (i in 0..segments) {
        val t = i.toFloat() / segments
        val x = width * t

        // Create a wavy pattern using sine-like curve
        val y = when {
            t < 0.25f -> {
                // First quarter: curve up
                height * (0.7f - 0.6f * (t / 0.25f))
            }
            t < 0.5f -> {
                // Second quarter: curve to middle
                val localT = (t - 0.25f) / 0.25f
                height * (0.1f + 0.5f * localT)
            }
            t < 0.75f -> {
                // Third quarter: curve down
                val localT = (t - 0.5f) / 0.25f
                height * (0.6f + 0.3f * localT)
            }
            else -> {
                // Fourth quarter: curve up sharply
                val localT = (t - 0.75f) / 0.25f
                height * (0.9f - 0.7f * localT)
            }
        }

        points.add(Offset(x, y))
    }

    return points
}

@Preview
@Composable
fun WavyLineComponentPreview() {
    PreviewComponent {
        WavyLinePreview()
    }
}

@Preview
@Composable
fun WavyLineAirBrushPreview() {
    PreviewComponent {
        WavyLinePreview(brush = AirBrush.default())
    }
}


