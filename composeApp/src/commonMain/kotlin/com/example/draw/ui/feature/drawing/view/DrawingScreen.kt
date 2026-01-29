package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import com.example.draw.ui.support_feature.brushConfig.brush.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.brushConfig.color.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.ellipseTool.mainComponent.EllipseToolButton
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoRedoButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawingScreen(
    val initState: DrawingState? = null
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DrawingScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val scope = rememberCoroutineScope()
        val drawingGraphicsLayer = rememberGraphicsLayer()

        var showLayerListPanel by remember { mutableStateOf(false) }

        LaunchedEffect(Unit){
            initState?.let {
                viewModel.onEvent(DrawingEvent.LoadInitialState(it))
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            floatingActionButton = {
                if(showLayerListPanel) {
                    state.canvas.activeLayer?.let { activeLayer ->
                        LayerListPanel(
                            activeLayer = activeLayer,
                            currentLayers = state.layers,
                            canvasWidth = state.canvas.width,
                            canvasHeight = state.canvas.height,
                            onAddLayer = {
                                viewModel.onEvent(DrawingEvent.AddLayer)
                            },
                            onSelectLayer = {
                                viewModel.onEvent(DrawingEvent.SelectLayer(it))
                            },
                            onToggleVisibility = {
                                viewModel.onEvent(DrawingEvent.ToggleLayerVisibility(it))
                            },
                            onDeleteLayer = {
                                viewModel.onEvent(DrawingEvent.DeleteLayer(it))
                            },
                            onReorderLayer = { fromIndex, toIndex ->
                                viewModel.onEvent(DrawingEvent.ReorderLayer(fromIndex, toIndex))
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                DrawingCanvasContent(
                    state = state,
                    viewModel = viewModel,
                    rootGraphicsLayer = drawingGraphicsLayer,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    ToolPanel(
                        shouldHideToolPanel = state.isUserDrawing,
                    ){
                        EllipseToolButton(
                            isActive = state.ellipseMode != null,
                            onToggleEllipseMode = {
                                if (state.ellipseMode != null) {
                                    viewModel.onEvent(DrawingEvent.ExitEllipseMode)
                                } else {
                                    viewModel.onEvent(DrawingEvent.EnterEllipseMode)
                                }
                            }
                        )
                        UndoRedoButton(
                            onRedo = if (state.canRedo) {
                                { viewModel.onEvent(DrawingEvent.Redo) }
                            } else null,
                            onUndo = if (state.canUndo) {
                                { viewModel.onEvent(DrawingEvent.Undo) }
                            } else null
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    ToolPanel(
                        appearFromBottom = true,
                        shouldHideToolPanel = state.isUserDrawing,
                    ){
                        ColorPickerButton(
                            initialBrush = state.currentBrush,
                            onBrushConfigFinished = { newBrush ->
                                viewModel.onEvent(DrawingEvent.ChangeBrush(newBrush))
                            }
                        )
                        BrushConfigButton(
                            currentBrush = state.currentBrush,
                            onBrushConfigFinished = { newBrush ->
                                viewModel.onEvent(DrawingEvent.ChangeBrush(newBrush))
                            }
                        )
                        CustomIconButton(
                            icon = Icons.Default.Save,
                        ) {
                            viewModel.onEvent(DrawingEvent.SaveDrawingProject(state))
                            scope.launch(Dispatchers.Default) {
                                //Chụp ảnh (Main Thread)
                                val bitmap = drawingGraphicsLayer.toImageBitmap()
                                viewModel.onEvent(DrawingEvent.SaveDrawing(bitmap))
                            }
                        }
                        LayerListPanelButton(
                            onClick = {
                                showLayerListPanel = !showLayerListPanel
                            }
                        )
                    }
                }

                if (showLayerListPanel) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { showLayerListPanel = false }
                                )
                            }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    PreviewComponent { DrawingScreen().Content() }
}
