package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.draw.data.model.base.DrawingPath
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.ui.feature.drawing.component.drawingInput
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.support_feature.brushConfig.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.colorPicker.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.colorPicker.mockData.MockColorPalette
import com.example.draw.ui.support_feature.layerConfig.component.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.layerConfig.model.LayerConfig
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoRedoButton
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

class DrawingScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DrawingScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        var showLayerListPanel by remember { mutableStateOf(false) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            floatingActionButton = {
                if(showLayerListPanel){


                    LayerListPanel(
                        onAddLayer = {},
                        onSelectLayer = {},
                        onToggleVisibility = {},
                        onDeleteLayer = {}
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
                            currentBrush = state.currentBrush,
                            onColorSelected = { newColor ->
                                val updatedBrush = state.currentBrush.updateColor(newColor.toArgb().toLong())
                                viewModel.onEvent(DrawingEvent.ChangeBrush(updatedBrush))
                            }
                        )
                        BrushConfigButton(
                            currentBrush = state.currentBrush,
                            onConfig = { newBrush ->
                                viewModel.onEvent(DrawingEvent.ChangeBrush(newBrush))
                            }
                        )
                    },
                    centerContent = {
                        // Add center content here for preview
                    },
                    rightContent = {
                        LayerListPanelButton(
                            onClick = {
                                showLayerListPanel = !showLayerListPanel
                            }
                        )
                    }
                )
            }
        ) {
            Box(

                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
                
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.White)
                ){
                    DrawingCanvas(
                        paths = state.completedDrawingPaths,
                        currentPath = state.currentDrawingPath,
                        isEraserMode = false,
                        currentTouchPosition = state.currentTouchPosition,
                        brushSize = state.currentBrush.size,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawingInput(
                                onDragStart = { offset ->
                                    viewModel.onEvent(DrawingEvent.StartDrawing(
                                        currentTouchPosition = offset,
                                    ))
                                },
                                onDrag = { offset ->
                                    // DI CHUYỂN: Thêm điểm vào nét đang vẽ
                                    viewModel.onEvent(DrawingEvent.UpdateDrawing(
                                        currentTouchPosition = offset,
                                    ))
                                },
                                onDragEnd = {
                                    // KẾT THÚC: Lưu nét vẽ vào danh sách chính
                                    viewModel.onEvent(DrawingEvent.EndDrawing)
                                }
                            )
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
