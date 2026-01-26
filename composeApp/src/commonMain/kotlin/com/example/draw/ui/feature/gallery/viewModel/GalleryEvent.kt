package com.example.draw.ui.feature.gallery.viewModel

sealed interface GalleryEvent {
    object LoadDrawings : GalleryEvent
    object CreateDrawing : GalleryEvent
    data class DeleteDrawing(val drawing: String) : GalleryEvent
    data class EditDrawing(val drawing: String) : GalleryEvent
}