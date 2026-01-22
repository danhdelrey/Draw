package com.example.draw.ui.support_feature.colorPicker.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.colorPicker.mockData.MockColorPalette


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    currentColor: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    var selectedColor by remember { mutableStateOf(currentColor) }

    CustomBottomSheet(
        onDismissRequest = onDismissRequest
    ){
        // 1. Phần Header với nét vẽ (Wavy Line)
        WavyLinePreviewWithBackground()

        // 2. Phần lưới chọn màu
        ColorGrid(
            colors = MockColorPalette.toList(),
            selectedColor = selectedColor,
            onColorClick = {
                selectedColor = it
                onColorSelected(it)
            }
        )
    }
}


@Preview
@Composable
fun ColorPickerBottomSheetPreview() {
    PreviewComponent {
        ColorPickerBottomSheet(
            currentColor = MockColorPalette.MIDNIGHTBLUE.color,
            onDismissRequest = {},
            onColorSelected = {},
        )
    }
}





