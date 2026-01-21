package com.example.draw.ui.support_feature.colorPicker.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.colorPicker.component.ColorPickerBottomSheet
import com.example.draw.ui.support_feature.colorPicker.mockData.colorPalette

@Composable
fun ColorPickerButton(
    onColorSelected: (Color) -> Unit = {}
) {
    var selectedColor by remember { mutableStateOf(colorPalette[2]) }
    var showColorPickerBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier

            .height(48.dp)
            .width(48.dp)
            .clip(CircleShape)
            .border(
                width = 5.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .background(selectedColor)
            .clickable{
                showColorPickerBottomSheet = true
            }
    )

    if(showColorPickerBottomSheet) {
        ColorPickerBottomSheet(
            onDismissRequest = {
                showColorPickerBottomSheet = false
            },
            onColorSelected = { color ->
                onColorSelected(color)
                selectedColor = color
            }
        )
    }
}

@Preview
@Composable
fun ColorPickerButtonPreview() {
    PreviewComponent {
        ColorPickerButton()
    }
}