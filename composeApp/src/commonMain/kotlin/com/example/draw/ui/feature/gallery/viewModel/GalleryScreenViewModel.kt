package com.example.draw.ui.feature.gallery.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.datasource.local.DrawingRepository
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GalleryScreenViewModel(
    private val drawingRepository: DrawingRepository
) : ScreenModel {
    private val _state = MutableStateFlow(GalleryState(isLoading = true))
    val state = _state.asStateFlow()

    private val _effect = Channel<GalleryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()


    fun onEvent(event: GalleryEvent) {
        screenModelScope.launch{
            when (event) {
                is GalleryEvent.LoadDrawingProjects -> loadDrawingsProjects()
                is GalleryEvent.CreateDrawingProject -> createDrawingProject(event.canvasConfig, event.projectName)
                is GalleryEvent.DeleteDrawingProject -> deleteDrawingProject(event.name)
                is GalleryEvent.EditDrawingProject -> editDrawingProject(event.name)
            }
        }
    }

    private suspend fun editDrawingProject(name: String) {

    }

    private suspend fun deleteDrawingProject(name: String) {

    }

    private suspend fun createDrawingProject(canvasConfig: CanvasConfig, projectName: String) {
        val newProject = drawingRepository.createDrawingProject(
            canvasConfig = canvasConfig,
            projectName = projectName
        )
        _effect.send(GalleryEffect.CreateDrawingProjectSuccess(drawingState = DrawingState.fromDrawingProject(newProject)))

    }

    private suspend fun loadDrawingsProjects() {
        val projects = drawingRepository.getAllDrawingProjects()
        _state.value = _state.value.copy(isLoading = false, drawingProjects = projects)
    }


}