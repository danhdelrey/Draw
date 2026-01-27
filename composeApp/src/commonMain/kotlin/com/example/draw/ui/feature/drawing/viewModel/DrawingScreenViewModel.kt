package com.example.draw.ui.feature.drawing.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.datasource.local.DrawingRepository
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.util.currentTimeMillis
import com.example.draw.data.model.util.generateId
import com.example.draw.data.repository.ImageRepository
import com.example.draw.platform.util.toPngByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the drawing screen.
 *
 * Design improvements:
 * - Uses DrawingCanvas for centralized state
 * - Leverages factory methods from models
 * - Enhanced command pattern support
 */
class DrawingScreenViewModel(
    private val imageRepository: ImageRepository,
    private val drawingRepository: DrawingRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    // Undo/Redo stacks
    private val undoStack = ArrayDeque<DrawingCommand>()
    private val redoStack = ArrayDeque<DrawingCommand>()

    fun onEvent(event: DrawingEvent) {
        screenModelScope.launch {
            when (event) {
                // --- DRAWING EVENTS ---
                is DrawingEvent.StartDrawing -> handleStartDrawing(event)
                is DrawingEvent.UpdateDrawing -> handleUpdateDrawing(event)
                is DrawingEvent.EndDrawing -> handleEndDrawing()

                // --- UNDO/REDO ---
                is DrawingEvent.Undo -> handleUndo()
                is DrawingEvent.Redo -> handleRedo()

                // --- LAYER MANAGEMENT ---
                is DrawingEvent.AddLayer -> handleAddLayer()
                is DrawingEvent.DeleteLayer -> handleDeleteLayer(event)
                is DrawingEvent.ToggleLayerVisibility -> handleToggleLayerVisibility(event)
                is DrawingEvent.SelectLayer -> handleSelectLayer(event)

                // --- BRUSH CONFIGURATION ---
                is DrawingEvent.ChangeBrush -> handleChangeBrush(event)

                // --- SAVE ---
                is DrawingEvent.SaveDrawing -> handleSaveDrawing(event)
                is DrawingEvent.SaveDrawingProject -> handleSaveDrawingProject(event.state)
            }
        }
    }

    private fun generateRandomProjectName(): String {
        val timestamp = currentTimeMillis()
        return "drawing_project_$timestamp.json"
    }

    private suspend fun handleSaveDrawingProject(state: DrawingState) {
        println("⏳ Saving drawing project...")
        val result = drawingRepository.saveDrawingProject(
            state.toDrawingProject(
                projectName = generateRandomProjectName()
            )
        )
        if(result){
            println("✓ Drawing project saved successfully.")
        } else {
            println("✗ Failed to save drawing project.")
        }
    }

    // --- Event Handlers ---

    private fun handleStartDrawing(event: DrawingEvent.StartDrawing) {
        _state.value = _state.value.copy(
            currentDrawingPath = DrawingPath.create(
                initialPoint = event.currentTouchPosition,
                brush = _state.value.currentBrush
            ),
            currentTouchPosition = event.currentTouchPosition
        )
    }

    private fun handleUpdateDrawing(event: DrawingEvent.UpdateDrawing) {
        val currentPath = _state.value.currentDrawingPath ?: return
        _state.value = _state.value.copy(
            currentDrawingPath = currentPath.addPoint(event.currentTouchPosition),
            currentTouchPosition = event.currentTouchPosition
        )
    }

    private fun handleEndDrawing() {
        val currentPath = _state.value.currentDrawingPath ?: return
        val activeLayerId = _state.value.canvas.activeLayerId

        // Create and execute command
        val command = AddPathCommand(
            layerId = activeLayerId,
            path = currentPath
        )
        performCommand(command)

        // Clear ephemeral state
        _state.value = _state.value.copy(
            currentDrawingPath = null,
            currentTouchPosition = null
        )
    }

    private fun handleUndo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeLast()
            val newState = command.undo(_state.value)
            _state.value = newState

            redoStack.addLast(command)
            updateUndoRedoAvailability()
        }
    }

    private fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            val newState = command.execute(_state.value)
            _state.value = newState

            undoStack.addLast(command)
            updateUndoRedoAvailability()
        }
    }

    private fun handleAddLayer() {
        val layerCount = _state.value.layers.size
        val newLayer = VectorLayer.create(
            id = generateId(),
            name = "Layer ${layerCount + 1}"
        )
        val command = AddLayerCommand(newLayer)
        performCommand(command)
    }

    private fun handleDeleteLayer(event: DrawingEvent.DeleteLayer) {
        // Don't allow deleting the default layer
        if (event.layer.id == "default_layer") return

        val index = _state.value.layers.indexOfFirst { it.id == event.layer.id }
        if (index != -1 && event.layer is VectorLayer) {
            val command = DeleteLayerCommand(event.layer, index)
            performCommand(command)
        }
    }

    private fun handleToggleLayerVisibility(event: DrawingEvent.ToggleLayerVisibility) {
        val command = ToggleLayerVisibilityCommand(event.layer.id)
        performCommand(command)
    }

    private fun handleSelectLayer(event: DrawingEvent.SelectLayer) {
        // Select layer doesn't change data, only view state
        // No undo/redo needed
        val updatedCanvas = _state.value.canvas.setActiveLayer(event.layer.id)
        _state.value = _state.value.copy(canvas = updatedCanvas)
    }

    private fun handleChangeBrush(event: DrawingEvent.ChangeBrush) {
        _state.value = _state.value.copy(currentBrush = event.brush)
    }

    private fun handleSaveDrawing(event: DrawingEvent.SaveDrawing) {
        screenModelScope.launch {
            val timestamp = currentTimeMillis()
            val name = "drawing_$timestamp"
            val bytes = event.imageBitmap.toPngByteArray()
            val result = imageRepository.saveImage(bytes, name)

            // TODO: Use effect channel for UI feedback instead of println
            if (result) {
                println("✓ Saved successfully: $name")
            } else {
                println("✗ Save failed: $name")
            }
        }
    }

    // --- Command Execution ---

    /**
     * Core command execution logic:
     * 1. Execute the command
     * 2. Update state
     * 3. Add to undo stack
     * 4. Clear redo stack (history branching)
     */
    private fun performCommand(command: DrawingCommand) {
        val newState = command.execute(_state.value)
        _state.value = newState

        undoStack.addLast(command)
        redoStack.clear() // Important: clear redo when new action is performed

        updateUndoRedoAvailability()
    }

    /**
     * Update undo/redo button availability
     */
    private fun updateUndoRedoAvailability() {
        _state.value = _state.value.copy(
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty()
        )
    }
}



