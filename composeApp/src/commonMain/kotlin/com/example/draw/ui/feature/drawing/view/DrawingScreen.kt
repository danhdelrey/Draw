package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.support_feature.brushConfig.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.colorPicker.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoRedoButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawingScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DrawingScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val scope = rememberCoroutineScope()
        val drawingGraphicsLayer = rememberGraphicsLayer()

        var showLayerListPanel by remember { mutableStateOf(false) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            floatingActionButton = {
                if(showLayerListPanel){


                    LayerListPanel(
                        activeLayer = state.currentActiveLayer,
                        currentLayers = state.currentLayers,
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
                        }
                    )
                }
            },
            topBar = {
                ToolPanel {
                    UndoRedoButton()
                }
            },
            bottomBar = {
                ToolPanel(
                    leftContent = {
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
                    },
                    centerContent = {
                        // Add center content here for preview
                    },
                    rightContent = {
                        CustomIconButton(
                            icon = Icons.Default.Save,
                        ) {
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
                )
            }
        ) {
            DrawingCanvasContent(
                state = state,
                viewModel = viewModel,
                rootGraphicsLayer = drawingGraphicsLayer
            )
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    PreviewComponent { DrawingScreen().Content() }
}
