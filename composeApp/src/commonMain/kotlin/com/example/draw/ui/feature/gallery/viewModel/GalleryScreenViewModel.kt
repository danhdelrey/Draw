package com.example.draw.ui.feature.gallery.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.draw.data.datasource.local.DrawingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GalleryScreenViewModel(
    private val drawingRepository: DrawingRepository
) : ScreenModel {
    private val _state = MutableStateFlow(GalleryState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        onEvent(GalleryEvent.LoadDrawings)
    }

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.LoadDrawings -> loadDrawings()
            is GalleryEvent.CreateDrawing -> createDrawing()
            is GalleryEvent.DeleteDrawing -> deleteDrawing(event.drawing)
            is GalleryEvent.EditDrawing -> editDrawing(event.drawing)
        }
    }

    private fun deleteDrawing(drawing: String) {}

    private fun createDrawing() {
        TODO("Not yet implemented")
    }

    private fun loadDrawings() {
        TODO("Not yet implemented")
    }
    private fun editDrawing(drawing: String) {
        TODO("Not yet implemented")
    }
}