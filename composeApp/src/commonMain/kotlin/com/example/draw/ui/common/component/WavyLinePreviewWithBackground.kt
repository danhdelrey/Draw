package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush as ComposeBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RectangleShape), // Clip content to avoid any overflow artifacts
        contentAlignment = Alignment.Center
    ) {
        CheckerboardBackground(
            modifier = Modifier.fillMaxSize()
        )

        // Wrapper for content that interacts with the background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Use offscreen compositing for EraserBrush so it blends with the gradient
                    // instead of clearing the destination (which includes Checkerboard and window)
                    if (brush is EraserBrush) {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // For EraserBrush, add a gradient background to erase
            if (brush is EraserBrush) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            ComposeBrush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8BC34A),
                                    Color(0xFF03A9F4)
                                )
                            )
                        )
                )
            }

            WavyLinePreview(brush = brush)
        }
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

@Preview
@Composable
fun WavyLinePreviewEraserBrushWithBackgroundPreview() {
    PreviewComponent {
        WavyLinePreviewWithBackground(brush = EraserBrush.default())
    }
}
