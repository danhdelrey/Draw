package com.example.draw.ui.feature.gallery.viewModel

import com.example.draw.data.model.canvas.CanvasConfig

sealed interface GalleryEvent {
    object LoadDrawingProjects : GalleryEvent
    data class CreateDrawingProject(
        val canvasConfig: CanvasConfig,
        val projectName: String,
    ) : GalleryEvent
    data class DeleteDrawingProject(val name: String) : GalleryEvent
    data class EditDrawingProject(val name: String) : GalleryEvent
}