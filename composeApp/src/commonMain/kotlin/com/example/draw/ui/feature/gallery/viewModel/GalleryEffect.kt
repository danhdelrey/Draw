package com.example.draw.ui.feature.gallery.viewModel

import com.example.draw.ui.feature.drawing.viewModel.DrawingState

sealed interface GalleryEffect{
    data class CreateDrawingProjectSuccess(val drawingState: DrawingState) : GalleryEffect
}