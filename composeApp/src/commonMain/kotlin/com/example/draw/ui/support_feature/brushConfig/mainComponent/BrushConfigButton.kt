package com.example.draw.ui.support_feature.brushConfig.mainComponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.component.BrushConfigBottomSheet
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

@Composable
fun BrushConfigButton(
    currentBrush: Brush,
    onConfig: (Brush) -> Unit = {}
){
    var showBrushConfigBottomSheet by remember { mutableStateOf(false) }

    ImageButton(
        imageResource = Res.drawable.solid_brush,
        isSelected = true,
        onClick = {
            showBrushConfigBottomSheet = true
        }
    )

    if(showBrushConfigBottomSheet){
        BrushConfigBottomSheet(
            currentBrush = currentBrush,
            onDismissRequest = {
                showBrushConfigBottomSheet = false
            },
            onBrushConfig = { newBrush ->
                onConfig(newBrush)
            }
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