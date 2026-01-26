package com.example.draw.ui.feature.gallery.viewModel

data class GalleryState(
    val isLoading: Boolean = false,
    val drawings: List<String> = emptyList()
)
