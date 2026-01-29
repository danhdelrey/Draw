package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.AirBrush
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.preview.PreviewComponent

/**
 * Preview component with background for brush visualization.
 * Background color adapts based on brush type for better visibility.
 */
@Composable
fun WavyLinePreviewWithBackground(brush: Brush = SolidBrush.default()) {
    // Choose background color based on brush type for best visibility
    val backgroundColor = when (brush) {
        is EraserBrush -> Color.Black // Dark background to show eraser effect
        is AirBrush -> MaterialTheme.colorScheme.surface // Neutral for spray visibility
        else -> MaterialTheme.colorScheme.surface // Default surface color
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CheckerboardBackground(
            modifier = Modifier.fillMaxSize()
        )
        WavyLinePreview(brush = brush)
    }
}

@Preview
@Composable
fun WavyLinePreviewWithBackgroundPreview() {
    PreviewComponent {
        WavyLinePreviewWithBackground()
    }
}

@Preview
@Composable
fun WavyLinePreviewAirBrushWithBackgroundPreview() {
    PreviewComponent {
        WavyLinePreviewWithBackground(brush = AirBrush.default())
    }
}

