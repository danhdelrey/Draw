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
    currentBrush: Brush,
    onDismissRequest: () -> Unit = {},
    onBrushConfig: (Brush) -> Unit = {}
) {



    CustomBottomSheet(
        onDismissRequest = onDismissRequest
    ){
        WavyLinePreviewWithBackground(currentBrush)
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 30.dp)
        ) {
            SliderWithLabels(
                label = "Size",
                valueRange = 1f..100f,
                initialValue = currentBrush.size,
                onValueChange = {
                    onBrushConfig(currentBrush.updateSize(it))
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            SliderWithLabels(
                label = "Opacity",
                valueRange = 0f..100f,
                initialValue = currentBrush.opacity * 100f,
                valueSuffix = "%",
                onValueChange = {
                    onBrushConfig(currentBrush.updateOpacity(it / 100f))
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection(
                currentBrush = currentBrush,
                onBrushSelected = {
                    onBrushConfig(it)
                }
            )

        }
    }
}

@Preview
@Composable
fun BrushConfigBottomSheetPreview() {
    PreviewComponent {
        BrushConfigBottomSheet(currentBrush = SolidBrush())
    }
}