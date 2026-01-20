package com.example.draw.ui.feature.brushConfig.mainComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.SliderWithLabels
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.brushConfig.component.BrushSelection

@Composable
fun BrushConfigBottomSheet() {
    CustomBottomSheet(
        onDismissRequest = {}
    ){
        WavyLinePreviewWithBackground(selectedColor = Color.Blue)
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            SliderWithLabels(
                label = "Size",
                valueRange = 1f..100f,
                currentValue = 50f,
                value = "50",
                onSizeChange = {}
            )
            Spacer(modifier = Modifier.height(15.dp))
            SliderWithLabels(
                label = "Opacity",
                valueRange = 0f..1f,
                currentValue = 1f,
                value = "100%",
                onSizeChange = {}
            )
            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection()

        }
    }
}

@Preview
@Composable
fun BrushConfigBottomSheetPreview() {
    PreviewComponent {
        BrushConfigBottomSheet()
    }
}