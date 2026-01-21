package com.example.draw.ui.support_feature.brushConfig.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.SliderWithLabels
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun BrushConfigBottomSheet(
    onDissmissRequest: () -> Unit = {}
) {
    var selectedSize by remember { mutableStateOf(50f) }
    var selectedOpacity by remember { mutableStateOf(1f) }
    var selectedBrush by remember { mutableStateOf<Brush>(SolidBrush()) }


    CustomBottomSheet(
        onDismissRequest = onDissmissRequest
    ){
        WavyLinePreviewWithBackground(selectedColor = Color.Blue, strokeWidth = selectedSize.dp, opacity = selectedOpacity)
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 30.dp)
        ) {
            SliderWithLabels(
                label = "Size",
                valueRange = 1f..100f,
                initialValue = 50f,
                onValueChange = {
                    selectedSize = it
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            SliderWithLabels(
                label = "Opacity",
                valueRange = 0f..100f,
                initialValue = 100f,
                valueSuffix = "%",
                onValueChange = {
                    selectedOpacity = it / 100f
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection(
                onBrushSelected = {
                    selectedBrush = it
                }
            )

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