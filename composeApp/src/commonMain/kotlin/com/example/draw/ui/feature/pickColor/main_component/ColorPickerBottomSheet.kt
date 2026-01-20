package com.example.draw.ui.feature.pickColor.main_component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.feature.pickColor.component.ColorGrid
import com.example.draw.ui.feature.pickColor.mockData.colorPalette
import com.example.draw.ui.common.preview.PreviewComponent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    onDismissRequest: () -> Unit,
    initialColor: Color = colorPalette[2], // Mặc định chọn màu xanh như hình
    onColorSelected: (Color) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedColor by remember { mutableStateOf(initialColor) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp), // Hình gốc vuông góc trên cùng (tùy chọn) hoặc bo tròn
        containerColor = Color.White,
        dragHandle = null // Ẩn thanh kéo mặc định
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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





