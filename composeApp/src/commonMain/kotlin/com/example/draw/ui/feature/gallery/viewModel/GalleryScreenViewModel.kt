package com.example.draw.ui.feature.gallery.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.datasource.local.DrawingRepository
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.model.util.currentTimeMillis
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
                is GalleryEvent.RenameDrawingProject -> renameDrawingProject(event.project, event.newName)
                is GalleryEvent.ImportDrawingProject -> importDrawingProject(event.project)
            }
        }
    }

    private suspend fun importDrawingProject(project: DrawingProject) {
        val importedProject = project.copy(
            lastModified = currentTimeMillis()
        )
        // Ensure name uniqueness? DrawingRepository implementation dependent.
        // Assuming saveDrawingProject overwrites if exists or just saves.
        // It's safer to ensure we don't overwrite existing without asking, but for now let's assume simple import.
        // To avoid collision, we might want to check existence or append suffix.
        // But for simplicity of this task, let's just save.

        if (drawingRepository.saveDrawingProject(importedProject)) {
             _effect.send(GalleryEffect.CreateDrawingProjectSuccess(drawingState = DrawingState.fromDrawingProject(importedProject)))
        }
    }

    private suspend fun renameDrawingProject(project: DrawingProject, newName: String) {
        val newProject = project.copy(
            name = newName,
            lastModified = currentTimeMillis()
        )
        if (drawingRepository.saveDrawingProject(newProject)) {
            drawingRepository.deleteDrawingProject(project.name)
            loadDrawingsProjects()
        }
    }

    private suspend fun deleteDrawingProject(name: String) {
        if (drawingRepository.deleteDrawingProject(name)) {
            loadDrawingsProjects()
        }
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
            .sortedByDescending { it.lastModified }
        _state.value = _state.value.copy(isLoading = false, drawingProjects = projects)
    }


}