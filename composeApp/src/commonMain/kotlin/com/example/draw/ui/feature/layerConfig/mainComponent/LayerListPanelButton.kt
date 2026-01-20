package com.example.draw.ui.feature.layerConfig.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.layerConfig.component.LayerListPanel
import com.example.draw.ui.feature.layerConfig.model.LayerConfig
import org.jetbrains.compose.resources.painterResource

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