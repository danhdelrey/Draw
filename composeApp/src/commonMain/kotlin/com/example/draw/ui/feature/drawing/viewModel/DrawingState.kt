package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer

data class DrawingState(
    val currentBrush: Brush = SolidBrush(),
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,
    val currentActiveLayer: Layer = VectorLayer(id = "default_layer"),
    val currentLayers: List<Layer> = listOf(VectorLayer(id = "default_layer")),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
