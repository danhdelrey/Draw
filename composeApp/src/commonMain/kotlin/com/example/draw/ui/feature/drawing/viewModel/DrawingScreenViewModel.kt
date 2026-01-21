package com.example.draw.ui.feature.drawing.viewModel

import androidx.compose.ui.geometry.Offset
import cafe.adriel.voyager.core.model.ScreenModel
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.Brush
import com.example.draw.data.model.brush.SolidBrush
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DrawingScreenViewModel : ScreenModel {
    private val _currentBrush = MutableStateFlow<Brush>(SolidBrush())
    private val _currentDrawingPath = MutableStateFlow<DrawingPath?>(null)
    private val _completedDrawingPaths = MutableStateFlow(listOf<DrawingPath>())
    private val _currentTouchPosition = MutableStateFlow<Offset?>(null)

    val currentBrush = _currentBrush.asStateFlow()
    val currentDrawingPath = _currentDrawingPath.asStateFlow()
    val completedDrawingPaths = _completedDrawingPaths.asStateFlow()
    val currentTouchPosition = _currentTouchPosition.asStateFlow()

    

    fun onEvent(event: DrawingEvent){
        when(event){
            is DrawingEvent.StartDrawing -> {
                //do sth
            }
        }
    }


}