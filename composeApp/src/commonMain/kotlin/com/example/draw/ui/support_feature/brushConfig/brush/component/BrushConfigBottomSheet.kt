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
import com.example.draw.data.model.brush.BrushType
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
    // Determine the shared color to use.
    // If initial brush has a valid color (non-transparent), use it.
    // Otherwise (e.g. legacy Eraser with 0 color), use lastActiveColor.
    val activeColor = remember(initialBrush, lastActiveColor) {
        if (initialBrush.colorArgb != 0L) initialBrush.colorArgb else lastActiveColor
    }

    val brushConfigurations = remember {
        mutableMapOf<BrushType, Brush>().apply {
            put(initialBrush.type, initialBrush)
        }
    }

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
        Spacer(Modifier.height(16.dp))
        WavyLinePreviewWithBackground(newBrush)
        Column(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 30.dp)
        ) {
            androidx.compose.runtime.key(newBrush.type) {
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
            }

            Spacer(modifier = Modifier.height(25.dp))
            BrushSelection(
                initialBrush = newBrush,
                onBrushSelected = { selectedBrush ->
                    brushConfigurations[newBrush.type] = newBrush

                    var targetBrush = brushConfigurations.getOrElse(selectedBrush.type) {
                        selectedBrush
                    }

                    // Apply shared color to the target brush
                    targetBrush = targetBrush.updateColor(activeColor)

                    brushSize = targetBrush.size
                    brushOpacity = targetBrush.opacity
                    if (targetBrush is AirBrush) {
                        brushDensity = targetBrush.density
                    }

                    newBrush = targetBrush
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