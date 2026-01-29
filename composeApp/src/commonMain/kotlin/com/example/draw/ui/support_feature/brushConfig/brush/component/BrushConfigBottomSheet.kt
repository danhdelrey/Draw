package com.example.draw.ui.support_feature.brushConfig.brush.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.AirBrush
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.BrushProperties
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.SliderWithLabels
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun BrushConfigBottomSheet(
    initialBrush: Brush,
    lastActiveColor: Long = 0xFF000000,
    onBrushConfigFinished: (Brush) -> Unit = {},
) {
    var brushSize by remember { mutableFloatStateOf(initialBrush.size) }
    var brushOpacity by remember { mutableFloatStateOf(initialBrush.opacity) }
    var brushDensity by remember {
        mutableFloatStateOf(
            if (initialBrush is AirBrush) initialBrush.density else 0.5f
        )
    }
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
            if (newBrush !is EraserBrush){
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
            }

            // Show density slider only for AirBrush
            if (newBrush is AirBrush) {
                Spacer(modifier = Modifier.height(15.dp))
                SliderWithLabels(
                    label = "Density",
                    valueRange = 0f..100f,
                    initialValue = brushDensity * 100f,
                    valueSuffix = "%",
                    onValueChange = {
                        brushDensity = it / 100f
                        newBrush = newBrush.updateProperties(
                            BrushProperties(mapOf(BrushProperties.DENSITY to brushDensity))
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection(
                initialBrush = newBrush,
                onBrushSelected = { selectedBrush ->
                    // Preserve size, opacity, and color when switching brush type
                    val targetColor = if (initialBrush is EraserBrush) {
                        lastActiveColor
                    } else {
                        initialBrush.colorArgb
                    }

                    var updatedBrush = selectedBrush
                        .updateSize(brushSize)
                        .updateOpacity(brushOpacity)
                        .updateColor(targetColor)

                    // If switching to AirBrush, set density
                    if (updatedBrush is AirBrush) {
                        brushDensity = updatedBrush.density
                        updatedBrush = updatedBrush.updateProperties(
                            BrushProperties(mapOf(BrushProperties.DENSITY to brushDensity))
                        )
                    }

                    newBrush = updatedBrush
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