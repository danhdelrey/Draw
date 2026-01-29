package com.example.draw.ui.support_feature.brushConfig.brush.mainComponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.brush.component.BrushConfigBottomSheet
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.eraser
import draw.composeapp.generated.resources.solid_brush

@Composable
fun BrushConfigButton(
    currentBrush: Brush,
    onBrushConfigFinished: (Brush) -> Unit = {}
){
    var showBrushConfigBottomSheet by remember { mutableStateOf(false) }
    var lastActiveColor by remember { mutableStateOf(0xFF000000) }

    LaunchedEffect(currentBrush) {
        if (currentBrush !is EraserBrush) {
            lastActiveColor = currentBrush.colorArgb
        }
    }

    ImageButton(
        imageResource = currentBrush.imageResource,
        isSelected = true,
        onClick = {
            showBrushConfigBottomSheet = true
        }
    )

    if(showBrushConfigBottomSheet){
        BrushConfigBottomSheet(
            initialBrush = currentBrush,
            lastActiveColor = lastActiveColor,
            onBrushConfigFinished = {
                showBrushConfigBottomSheet = false
                onBrushConfigFinished(it)
            },

        )
    }
}

@Preview
@Composable
fun BrushConfigButtonPreview(){
    PreviewComponent {
        BrushConfigButton(currentBrush = SolidBrush())
    }
}