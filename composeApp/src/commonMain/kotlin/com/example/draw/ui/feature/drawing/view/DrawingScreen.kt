package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.draw.ui.common.component.ImageButton
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.event.DrawingEvent
import com.example.draw.ui.feature.drawing.utils.drawingInput
import com.example.draw.ui.support_feature.brushConfig.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.colorPicker.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.layerConfig.component.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.layerConfig.model.LayerConfig
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoRedoButton
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

class DrawingScreen : Screen {
    @Composable
    override fun Content() {

        val graphicsLayer = rememberGraphicsLayer()
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
                        ColorPickerButton()
                        BrushConfigButton()
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
            DrawingTestScreen()
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    PreviewComponent { DrawingScreen().Content() }
}
