package com.example.draw.ui.support_feature.brushConfig.brush.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.EraserBrush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun BrushSelection(
    initialBrush: Brush,
    onBrushSelected: (Brush) -> Unit = {}
) {
    var currentBrush by remember { mutableStateOf(initialBrush) }
    val brushList = listOf<Brush>(
        SolidBrush(),
        EraserBrush()
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(brushList) { brush ->
            ImageButton(
                brush.imageResource,
                isSelected = currentBrush::class == brush::class,
                onClick = {
                    currentBrush = brush
                    onBrushSelected(brush)
                }
            )
        }
    }
}

@Preview
@Composable
fun BrushSelectionPreview() {
    PreviewComponent {
        BrushSelection(initialBrush = EraserBrush())
    }
}