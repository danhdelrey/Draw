package com.example.draw.ui.feature.drawing.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.draw.data.datasource.local.DrawingRepository
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.shape.EllipseState
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

    private val _state = MutableStateFlow(DrawingState(projectName = generateRandomProjectName()))
    val state = _state.asStateFlow()

    // Undo/Redo stacks
    private val undoStack = ArrayDeque<DrawingCommand>()
    private val redoStack = ArrayDeque<DrawingCommand>()

    fun onEvent(event: DrawingEvent) {
        screenModelScope.launch {
            when (event) {
                is DrawingEvent.LoadInitialState -> handleLoadInitialState(event.state)
                // --- DRAWING EVENTS ---
                is DrawingEvent.StartDrawing -> handleStartDrawing(event)
                is DrawingEvent.UpdateDrawing -> handleUpdateDrawing(event)
                is DrawingEvent.EndDrawing -> handleEndDrawing()
                is DrawingEvent.CancelDrawing -> handleCancelDrawing()

                // --- UNDO/REDO ---
                is DrawingEvent.Undo -> handleUndo()
                is DrawingEvent.Redo -> handleRedo()

                // --- LAYER MANAGEMENT ---
                is DrawingEvent.AddLayer -> handleAddLayer()
                is DrawingEvent.DeleteLayer -> handleDeleteLayer(event)
                is DrawingEvent.ToggleLayerVisibility -> handleToggleLayerVisibility(event)
                is DrawingEvent.ChangeLayerOpacity -> handleChangeLayerOpacity(event)
                is DrawingEvent.ReorderLayer -> handleReorderLayer(event)
                is DrawingEvent.SelectLayer -> handleSelectLayer(event)
                is DrawingEvent.InvertLayer -> handleInvertLayer(event)
                is DrawingEvent.FlipLayerHorizontal -> handleFlipLayerHorizontal(event)
                is DrawingEvent.FlipLayerVertical -> handleFlipLayerVertical(event)
                is DrawingEvent.EnterTransformLayerMode -> handleEnterTransformLayerMode()
                is DrawingEvent.ExitTransformLayerMode -> handleExitTransformLayerMode()
                is DrawingEvent.ConfirmTransformLayer -> handleConfirmTransformLayer()

                // --- BRUSH CONFIGURATION ---
                is DrawingEvent.ChangeBrush -> handleChangeBrush(event)

                // --- SAVE ---
                is DrawingEvent.SaveDrawing -> handleSaveDrawing(event)
                is DrawingEvent.SaveDrawingProject -> handleSaveDrawingProject(event.state)

                // --- ELLIPSE DRAWING MODE ---
                is DrawingEvent.EnterEllipseMode -> handleEnterEllipseMode()
                is DrawingEvent.ExitEllipseMode -> handleExitEllipseMode()
                is DrawingEvent.UpdateEllipseCenter -> handleUpdateEllipseCenter(event)
                is DrawingEvent.UpdateEllipseRadii -> handleUpdateEllipseRadii(event)
                is DrawingEvent.UpdateEllipseRotation -> handleUpdateEllipseRotation(event)
                is DrawingEvent.UpdateEllipseScale -> handleUpdateEllipseScale(event)
                is DrawingEvent.UpdateEllipseState -> handleUpdateEllipseState(event)
            }
        }
    }

    private fun handleConfirmTransformLayer() {
        // Simply exit the transformation mode for now
        _state.value = _state.value.copy(isInLayerTransformationMode = false)
    }

    private fun handleExitTransformLayerMode() {
        _state.value = _state.value.copy(isInLayerTransformationMode = false)
    }

    private fun handleEnterTransformLayerMode() {
        _state.value = _state.value.copy(isInLayerTransformationMode = true)
    }


    private fun handleLoadInitialState(state: DrawingState) {
        println("⏳ Loading drawing project...")
        _state.value = state
        // Clear undo/redo stacks on load
        undoStack.clear()
        redoStack.clear()
        updateUndoRedoAvailability()
        println("✓ Drawing project loaded successfully.")
    }

    private fun generateRandomProjectName(): String {
        val timestamp = currentTimeMillis()
        return "drawing_project_$timestamp.json"
    }

    private suspend fun handleSaveDrawingProject(state: DrawingState) {
        println("⏳ Saving drawing project...")
        val result = drawingRepository.saveDrawingProject(
            state.toDrawingProject(
                projectName = state.projectName
            )
        )
        if(result){
            println("✓ Drawing project saved successfully.")
        } else {
            println("✗ Failed to save drawing project.")
        }
    }

    private fun triggerAutoSave() {
        screenModelScope.launch {
            val result = drawingRepository.saveDrawingProject(
                _state.value.toDrawingProject(
                    projectName = _state.value.projectName
                )
            )
            if (result) {
                println("✓ Auto-saved project: ${_state.value.projectName}")
            }
        }
    }

    // --- Event Handlers ---

    private fun handleStartDrawing(event: DrawingEvent.StartDrawing) {
        val activeLayer = _state.value.canvas.activeLayer
        if (activeLayer == null || activeLayer.isLocked) {
            return
        }

        _state.value = _state.value.copy(
            isUserDrawing = true,
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
            isUserDrawing = true,
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
            isUserDrawing = false,
            currentDrawingPath = null,
            currentTouchPosition = null
        )
    }

    private fun handleCancelDrawing() {
        // Clear the current drawing path without saving
        _state.value = _state.value.copy(
            isUserDrawing = false,
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
            triggerAutoSave()
        }
    }

    private fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            val newState = command.execute(_state.value)
            _state.value = newState

            undoStack.addLast(command)
            updateUndoRedoAvailability()
            triggerAutoSave()
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
        // Don't allow deleting locked layers
        if (event.layer.isLocked) return

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

    private fun handleChangeLayerOpacity(event: DrawingEvent.ChangeLayerOpacity) {
        val currentOpacity = event.layer.opacity
        if (currentOpacity == event.opacity) return

        val command = UpdateLayerOpacityCommand(
            layerId = event.layer.id,
            oldOpacity = currentOpacity,
            newOpacity = event.opacity
        )
        performCommand(command)
    }

    private fun handleReorderLayer(event: DrawingEvent.ReorderLayer) {
        val command = ReorderLayerCommand(event.fromIndex, event.toIndex)
        performCommand(command)
    }

    private fun handleSelectLayer(event: DrawingEvent.SelectLayer) {
        val updatedCanvas = _state.value.canvas.setActiveLayer(event.layer.id)
        _state.value = _state.value.copy(canvas = updatedCanvas)
    }

    private fun handleInvertLayer(event: DrawingEvent.InvertLayer) {
        val command = InvertLayerCommand(event.layer.id)
        performCommand(command)
    }

    private fun handleFlipLayerHorizontal(event: DrawingEvent.FlipLayerHorizontal) {
        val command = FlipLayerHorizontalCommand(event.layer.id)
        performCommand(command)
    }

    private fun handleFlipLayerVertical(event: DrawingEvent.FlipLayerVertical) {
        val command = FlipLayerVerticalCommand(event.layer.id)
        performCommand(command)
    }

    // --- ELLIPSE MODE HANDLERS ---

    private fun handleEnterEllipseMode() {
        val ellipseState = EllipseState.createDefault(
            canvasWidth = _state.value.canvas.width,
            canvasHeight = _state.value.canvas.height
        )
        _state.value = _state.value.copy(ellipseMode = ellipseState)
    }

    private fun handleExitEllipseMode() {
        _state.value = _state.value.copy(ellipseMode = null)
    }

    private fun handleUpdateEllipseCenter(event: DrawingEvent.UpdateEllipseCenter) {
        val currentEllipse = _state.value.ellipseMode ?: return
        _state.value = _state.value.copy(
            ellipseMode = currentEllipse.updateCenter(event.center)
        )
    }

    private fun handleUpdateEllipseRadii(event: DrawingEvent.UpdateEllipseRadii) {
        val currentEllipse = _state.value.ellipseMode ?: return
        _state.value = _state.value.copy(
            ellipseMode = currentEllipse.updateRadii(event.radiusX, event.radiusY)
        )
    }

    private fun handleUpdateEllipseRotation(event: DrawingEvent.UpdateEllipseRotation) {
        val currentEllipse = _state.value.ellipseMode ?: return
        _state.value = _state.value.copy(
            ellipseMode = currentEllipse.updateRotation(event.rotation)
        )
    }

    private fun handleUpdateEllipseScale(event: DrawingEvent.UpdateEllipseScale) {
        val currentEllipse = _state.value.ellipseMode ?: return
        _state.value = _state.value.copy(
            ellipseMode = currentEllipse.scale(event.scaleFactor)
        )
    }

    private fun handleUpdateEllipseState(event: DrawingEvent.UpdateEllipseState) {
        _state.value = _state.value.copy(ellipseMode = event.ellipseState)
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
        triggerAutoSave()
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






















