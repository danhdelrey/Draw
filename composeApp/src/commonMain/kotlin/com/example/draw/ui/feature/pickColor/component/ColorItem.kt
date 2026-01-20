package com.example.draw.ui.feature.pickColor.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Kích thước vòng tròn
    val size = 40.dp

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            // Nếu được chọn thì thêm viền đậm
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color(0xFF1A1A2E), CircleShape) // Viền tối màu (Dark Blue/Black)
                } else Modifier
            )
    )
}

@Preview
@Composable
fun ColorItemPreview() {
    PreviewComponent {
        ColorItem(
            color = Color(0xFFFF0000),
            isSelected = true,
            onClick = {}
        )
    }
}