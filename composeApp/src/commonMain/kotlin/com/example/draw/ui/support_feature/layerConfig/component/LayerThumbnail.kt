package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.component.CanvasThumbnail

/**
 * Thumbnail preview for a vector layer.
 * Uses unified drawing logic from CanvasThumbnail to support all brush types.
 */
@Composable
fun LayerThumbnail(
    layer: VectorLayer,
    canvasWidth: Float,
    canvasHeight: Float,
    showTransparentBackground: Boolean = false,
    modifier: Modifier = Modifier
) {
    CanvasThumbnail(
        layers = listOf(layer.copy(isVisible = true)),
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        backgroundColor = Color.White, // Ignored if showTransparentBackground is true by CanvasThumbnail
        showTransparentBackground = showTransparentBackground,
        modifier = modifier
    )
}
