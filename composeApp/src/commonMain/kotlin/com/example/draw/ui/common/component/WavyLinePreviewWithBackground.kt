package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun WavyLinePreviewWithBackground(brush: Brush = SolidBrush()) {
    val backgroundColor = if(brush is SolidBrush) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Black
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
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