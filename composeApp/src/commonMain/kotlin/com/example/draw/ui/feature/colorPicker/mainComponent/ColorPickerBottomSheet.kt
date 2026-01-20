package com.example.draw.ui.feature.colorPicker.mainComponent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.feature.colorPicker.component.ColorGrid
import com.example.draw.ui.feature.colorPicker.mockData.colorPalette
import com.example.draw.ui.common.preview.PreviewComponent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    onDismissRequest: () -> Unit,
    initialColor: Color = colorPalette[2], // Mặc định chọn màu xanh như hình
    onColorSelected: (Color) -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    CustomBottomSheet(
        onDismissRequest = onDismissRequest
    ){
        // 1. Phần Header với nét vẽ (Wavy Line)
        WavyLinePreviewWithBackground(selectedColor = selectedColor)

        // 2. Phần lưới chọn màu
        ColorGrid(
            colors = colorPalette,
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
            onDismissRequest = {},
            onColorSelected = {}
        )
    }
}





