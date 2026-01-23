package com.example.draw.ui.support_feature.brushConfig.color.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.color.mockData.MockColorPalette


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    initialBrush: Brush,
    onBrushConfigFinished: (Brush) -> Unit = {},
) {

    var currentBrush by remember { mutableStateOf(initialBrush) }

    CustomBottomSheet(
        onDismissRequest = {
            onBrushConfigFinished(currentBrush)
        }
    ){
        // 1. Phần Header với nét vẽ (Wavy Line)
        WavyLinePreviewWithBackground(currentBrush)

        // 2. Phần lưới chọn màu
        ColorGrid(
            colors = MockColorPalette.toList(),
            selectedColor = Color(currentBrush.colorArgb),
            onColorClick = {
                currentBrush = currentBrush.updateColor(it.toArgb().toLong())
            }
        )
    }
}


@Preview
@Composable
fun ColorPickerBottomSheetPreview() {
    PreviewComponent {
        ColorPickerBottomSheet(
            initialBrush = SolidBrush(),

        )
    }
}





