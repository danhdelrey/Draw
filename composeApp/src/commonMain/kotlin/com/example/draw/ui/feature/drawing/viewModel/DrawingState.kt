package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer

data class DrawingState(
    var currentBrush: Brush = SolidBrush(),
    var currentTouchPosition: Offset? = null,
    var currentDrawingPath: DrawingPath? = null,
    var currentActiveLayer: Layer = VectorLayer(id = "default_layer"),
    var currentLayers: List<Layer> = listOf(VectorLayer(id = "default_layer")),
    var canUndo: Boolean = false,
    var canRedo: Boolean = false
)
