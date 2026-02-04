package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import com.example.draw.ui.support_feature.brushConfig.brush.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.brushConfig.color.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.ellipseTool.mainComponent.EllipseToolButton
import com.example.draw.ui.support_feature.rectangleTool.mainComponent.RectangleToolButton
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.saveImage.mainComponent.SaveImageButton
import com.example.draw.ui.support_feature.selection.mainComponent.SelectionToolButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.RedoButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawingScreen(
    val initState: DrawingState? = null
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DrawingScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val navigator = LocalNavigator.current

        val scope = rememberCoroutineScope()
        val drawingGraphicsLayer = rememberGraphicsLayer()

        var showLayerListPanel by remember { mutableStateOf(false) }

        LaunchedEffect(Unit){
            initState?.let {
                viewModel.onEvent(DrawingEvent.LoadInitialState(it))
            }
        }

        LaunchedEffect(state.isUserDrawing){
            //Ẩn panel khi đang vẽ
            if(state.isUserDrawing){
                showLayerListPanel = false
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
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

                if (showLayerListPanel) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showLayerListPanel = false
                            }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    ToolPanel(
                        shouldHideToolPanel = !state.isInLayerTransformationMode,
                    ){
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .width(40.dp)
                                .height(40.dp)
                                .clickable {
                                    viewModel.onEvent(DrawingEvent.ExitTransformLayerMode)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                imageVector = Icons.Default.Cancel,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .width(40.dp)
                                .height(40.dp)
                                .clickable {
                                    viewModel.onEvent(DrawingEvent.ConfirmTransformLayer)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                imageVector = Icons.Default.Check,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }

                    }
                    ToolPanel(
                        shouldHideToolPanel = state.isUserDrawing || state.isInLayerTransformationMode,
                    ){
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .width(40.dp)
                                .height(40.dp)
                                .clickable {
                                    navigator?.pop()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                imageVector = Icons.Default.ChevronLeft,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                        UndoButton(
                            onUndo = if (state.canUndo) {
                                { viewModel.onEvent(DrawingEvent.Undo) }
                            } else null
                        )
                        RedoButton(
                            onRedo = if (state.canRedo) {
                                { viewModel.onEvent(DrawingEvent.Redo) }
                            } else null
                        )
                        SaveImageButton {
                            viewModel.onEvent(DrawingEvent.SaveDrawingProject(state))
                            scope.launch(Dispatchers.Default) {
                                //Chụp ảnh (Main Thread)
                                val bitmap = drawingGraphicsLayer.toImageBitmap()
                                viewModel.onEvent(DrawingEvent.SaveDrawing(bitmap))
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if(showLayerListPanel) {
                            state.canvas.activeLayer?.let { activeLayer ->
                                Box(
                                    modifier = Modifier.padding(15.dp)
                                ) {
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
                                        onInvertLayer = {
                                            viewModel.onEvent(DrawingEvent.InvertLayer(it))
                                        },
                                        onFlipLayerHorizontal = {
                                            viewModel.onEvent(DrawingEvent.FlipLayerHorizontal(it))
                                        },
                                        onFlipLayerVertical = {
                                            viewModel.onEvent(DrawingEvent.FlipLayerVertical(it))
                                        },
                                        onOpacityChange = { layer, opacity ->
                                            viewModel.onEvent(DrawingEvent.ChangeLayerOpacity(layer, opacity))
                                        },
                                        onEnterTransformationMode = {
                                            viewModel.onEvent(DrawingEvent.EnterTransformLayerMode(it))
                                            showLayerListPanel = false
                                        },
                                        onReorderLayer = { fromIndex, toIndex ->
                                            viewModel.onEvent(
                                                DrawingEvent.ReorderLayer(
                                                    fromIndex,
                                                    toIndex
                                                )
                                            )
                                        },
                                        onMergeLayer = { fromIndex, toIndex ->
                                            viewModel.onEvent(
                                                DrawingEvent.MergeLayer(
                                                    fromIndex,
                                                    toIndex
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        ToolPanel(
                            appearFromBottom = true,
                            shouldHideToolPanel = state.isUserDrawing || state.isInLayerTransformationMode,
                        ) {
                            SelectionToolButton(

                            )
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
                            RectangleToolButton(
                                isActive = state.rectangleMode != null,
                                onToggleRectangleMode = {
                                    if (state.rectangleMode != null) {
                                        viewModel.onEvent(DrawingEvent.ExitRectangleMode)
                                    } else {
                                        viewModel.onEvent(DrawingEvent.EnterRectangleMode)
                                    }
                                }
                            )
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

                            LayerListPanelButton(
                                onClick = {
                                    showLayerListPanel = !showLayerListPanel
                                }
                            )
                        }
                    }
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
