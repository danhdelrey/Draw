package com.example.draw.ui.support_feature.layerConfig.mainComponent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.layerConfig.component.LayerListPanel
import com.example.draw.ui.support_feature.layerConfig.model.LayerConfig

@Composable
fun LayerListPanelButton(

) {
    var showLayerListPanel by remember { mutableStateOf(false) }

    CustomIconButton(
        icon = Icons.Default.Layers,
        enabled = true,
        onClick = {
            showLayerListPanel = !showLayerListPanel
        }
    )

    if(showLayerListPanel){
        val sampleLayers = listOf(
            LayerConfig(id = 1, isVisible = true),
            LayerConfig(id = 2, isVisible = false),
            LayerConfig(id = 3, isVisible = true)
        )

        LayerListPanel(
            layers = sampleLayers,
            selectedLayerId = 2,
            onAddLayer = {},
            onSelectLayer = {},
            onToggleVisibility = {},
            onDeleteLayer = {}
        )
    }



}

@Preview
@Composable
fun LayerListPanelButtonPreview() {
    PreviewComponent {
        LayerListPanelButton(

        )
    }
}