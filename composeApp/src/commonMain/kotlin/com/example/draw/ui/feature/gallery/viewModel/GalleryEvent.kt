package com.example.draw.ui.feature.gallery.viewModel

sealed interface GalleryEvent {
    object LoadDrawingProjects : GalleryEvent
    object CreateDrawingProject : GalleryEvent
    data class DeleteDrawingProject(val name: String) : GalleryEvent
    data class EditDrawingProject(val name: String) : GalleryEvent
}