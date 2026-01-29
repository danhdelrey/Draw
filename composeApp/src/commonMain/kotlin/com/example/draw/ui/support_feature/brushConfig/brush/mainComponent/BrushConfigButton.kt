package com.example.draw.ui.support_feature.brushConfig.brush.mainComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.brush.component.BrushConfigBottomSheet
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.eraser
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

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


    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .width(40.dp)
            .height(40.dp)
            .clickable {
                showBrushConfigBottomSheet = true
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            painter = painterResource(currentBrush.imageResource),
            contentDescription = null
        )
    }

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