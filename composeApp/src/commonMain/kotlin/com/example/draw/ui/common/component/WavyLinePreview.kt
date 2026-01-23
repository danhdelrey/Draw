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
    val segments = 100 // More points for smoother curve

    for (i in 0..segments) {
        val t = i.toFloat() / segments
        val x = width * t

        // Create smooth wavy pattern using sine wave
        // Two complete waves across the width
        val wave = kotlin.math.sin(t * 2 * kotlin.math.PI.toFloat() * 2)

        // Map sine wave (-1 to 1) to vertical space with padding
        // Center at 0.5, amplitude of 0.3 (30% of height on each side)
        val y = height * (0.5f + wave * 0.3f)

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


