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
    initialBrush: Brush,
    onBrushConfigFinished: (Brush) -> Unit = {},
) {

    var brushSize by remember { mutableStateOf(initialBrush.size) }
    var brushOpacity by remember { mutableStateOf(initialBrush.opacity) }
    var newBrush by remember { mutableStateOf(initialBrush) }



    CustomBottomSheet(
        onDismissRequest = {
            onBrushConfigFinished(newBrush)
        }
    ){
        WavyLinePreviewWithBackground(newBrush)
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 30.dp)
        ) {
            SliderWithLabels(
                label = "Size",
                valueRange = 1f..100f,
                initialValue = brushSize,
                onValueChange = {
                    brushSize = it
                    newBrush = newBrush.updateSize(it)
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            SliderWithLabels(
                label = "Opacity",
                valueRange = 0f..100f,
                initialValue = brushOpacity * 100f,
                valueSuffix = "%",
                onValueChange = {
                    brushOpacity = it / 100f
                    newBrush = newBrush.updateOpacity(it / 100f)
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection(
                initialBrush = newBrush,
                onBrushSelected = {
                    newBrush = it.updateSize(brushSize).updateOpacity(brushOpacity)
                }
            )

        }
    }
}

@Preview
@Composable
fun BrushConfigBottomSheetPreview() {
    PreviewComponent {
        BrushConfigBottomSheet(initialBrush = SolidBrush())
    }
}