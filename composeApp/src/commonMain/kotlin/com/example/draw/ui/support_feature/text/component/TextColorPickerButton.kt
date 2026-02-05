package com.example.draw.ui.support_feature.text.component

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
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.color.component.ColorPickerBottomSheet

@Composable
fun TextColorPickerButton(
    initialColor: Color,
    onPickedColor: (Color) -> Unit = {},
) {
    var showColorPickerBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .height(40.dp)
            .width(40.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                brush = linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                ),
                shape = CircleShape
            )
            .background(initialColor)
            .clickable {
                showColorPickerBottomSheet = true
            }
    )

    if(showColorPickerBottomSheet) {
        TextColorPickerBottomSheet(
            initialColor = initialColor,
            onSelectedColor = {
                showColorPickerBottomSheet = false
                onPickedColor(it)
            },
        )
    }
}

@Preview
@Composable
fun TextColorPickerButtonPreview() {
    PreviewComponent {
        TextColorPickerButton(
            initialColor = Color.Red,
        )
    }
}