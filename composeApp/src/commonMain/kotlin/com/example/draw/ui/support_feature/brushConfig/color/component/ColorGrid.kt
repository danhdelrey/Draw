package com.example.draw.ui.support_feature.brushConfig.color.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.color.mockData.MockColorPalette

@Composable
fun ColorGrid(
    colors: List<Color>,
    selectedColor: Color,
    onColorClick: (Color) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6), // 6 cột
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(colors) { color ->
            ColorItem(
                color = color,
                isSelected = color == selectedColor,
                onClick = { onColorClick(color) }
            )
        }
    }
}

@Preview
@Composable
fun ColorGridPreview() {
    PreviewComponent {
        ColorGrid(
            colors = MockColorPalette.toList(),
            selectedColor = MockColorPalette.MIDNIGHTBLUE.color,
            onColorClick = {}
        )
    }
}