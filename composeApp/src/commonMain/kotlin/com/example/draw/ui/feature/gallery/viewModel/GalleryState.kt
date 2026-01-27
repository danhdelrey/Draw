package com.example.draw.ui.feature.gallery.viewModel

import com.example.draw.data.model.serialization.DrawingProject

data class GalleryState(
    val isLoading: Boolean = false,
    val drawingProjects: List<DrawingProject> = emptyList()
)
