package com.example.draw.ui.feature.gallery.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.datasource.local.DrawingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryScreenViewModel(
    private val drawingRepository: DrawingRepository
) : ScreenModel {
    private val _state = MutableStateFlow(GalleryState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        onEvent(GalleryEvent.LoadDrawingProjects)
    }

    fun onEvent(event: GalleryEvent) {
        screenModelScope.launch{
            when (event) {
                is GalleryEvent.LoadDrawingProjects -> loadDrawingsProjects()
                is GalleryEvent.CreateDrawingProject -> createDrawingProject()
                is GalleryEvent.DeleteDrawingProject -> deleteDrawingProject(event.name)
                is GalleryEvent.EditDrawingProject -> editDrawingProject(event.name)
            }
        }
    }

    private suspend fun editDrawingProject(name: String) {}

    private suspend fun deleteDrawingProject(name: String) {

    }

    private suspend fun createDrawingProject() {
    }

    private suspend fun loadDrawingsProjects() {
        val projects = drawingRepository.getAllDrawingProjects()
        _state.value = _state.value.copy(isLoading = false, drawingProjects = projects)
    }


}