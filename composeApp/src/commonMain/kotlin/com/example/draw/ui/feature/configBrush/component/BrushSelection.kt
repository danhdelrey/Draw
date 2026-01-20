package com.example.draw.ui.feature.configBrush.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.preview.PreviewComponent
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.eraser
import draw.composeapp.generated.resources.solid_brush

@Composable
fun BrushSelection() {
    Row {
        ImageButton(
            Res.drawable.solid_brush,
            isSelected = true,
            onClick = {}
        )
        Spacer(modifier = Modifier.width(15.dp))
        ImageButton(
            Res.drawable.eraser,
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun BrushSelectionPreview() {
    PreviewComponent {
        BrushSelection()
    }
}