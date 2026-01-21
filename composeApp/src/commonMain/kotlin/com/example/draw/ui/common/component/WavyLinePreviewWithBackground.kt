package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.draw.ui.support_feature.colorPicker.mockData.colorPalette
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun WavyLinePreviewWithBackground(selectedColor: Color, strokeWidth: Dp, opacity: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFFF0EBF5)) // Màu nền tím nhạt
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        WavyLinePreview(color = selectedColor, strokeWidth = strokeWidth, opacity = opacity)
    }
}

@Preview
@Composable
fun WavyLinePreviewWithBackgroundPreview() {
    PreviewComponent {
        WavyLinePreviewWithBackground(selectedColor = colorPalette[2], strokeWidth = 20.dp, opacity = 1f)
    }
}