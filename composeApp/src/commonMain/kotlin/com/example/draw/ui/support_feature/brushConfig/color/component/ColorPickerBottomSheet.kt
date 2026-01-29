package com.example.draw.ui.support_feature.brushConfig.color.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.component.WavyLinePreviewWithBackground
import com.example.draw.ui.common.preview.PreviewComponent
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    initialBrush: Brush,
    onBrushConfigFinished: (Brush) -> Unit = {},
) {

    var currentBrush by remember { mutableStateOf(initialBrush) }

    val controller = rememberColorPickerController()

    LaunchedEffect(Unit) {
        controller.selectByColor(Color(initialBrush.colorArgb), false)
    }

    CustomBottomSheet(
        onDismissRequest = {
            onBrushConfigFinished(currentBrush)
        }
    ){
        Spacer(Modifier.height(16.dp))
        // 1. Phần Header với nét vẽ (Wavy Line)
        WavyLinePreviewWithBackground(currentBrush)

        // 2. HSV Color Picker with Hue & Alpha Sliders
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(10.dp),
                controller = controller,
                initialColor = Color(currentBrush.colorArgb),
                onColorChanged = { envelope ->
                    currentBrush = currentBrush.updateColor(envelope.color.toArgb().toLong())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(10.dp),
                controller = controller,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(10.dp),
                controller = controller,
            )
        }
    }
}


@Preview
@Composable
fun ColorPickerBottomSheetPreview() {
    PreviewComponent {
        ColorPickerBottomSheet(
            initialBrush = SolidBrush(
                size = 40f,
                colorArgb = Color.Blue.toArgb().toLong()
            ),

        )
    }
}
