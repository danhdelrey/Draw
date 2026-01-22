package com.example.draw.ui.support_feature.layerConfig.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.layerConfig.component.LayerItem

@Composable
fun LayerListPanel(
    currentLayers: List<Layer>,
    activeLayer: Layer,
    onAddLayer: () -> Unit,
    onSelectLayer: (Layer) -> Unit,
    onToggleVisibility: (Layer) -> Unit,
    onDeleteLayer: (Layer) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .height(500.dp)
            .background(Color(0xFF555555))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onAddLayer,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF6750A4), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Layer",
                    tint = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(
                items = currentLayers,
                key = { it.id }
            ) { layer ->
                LayerItem(
                    data = layer,
                    isSelected = layer.id == activeLayer.id,
                    onClick = { onSelectLayer(layer) },
                    onToggleVisibility = { onToggleVisibility(layer) },
                    onDelete = { onDeleteLayer(layer) }
                )
            }
        }
    }
}


@Preview
@Composable
fun LayerListPanelPreview() {
    PreviewComponent {
        val sampleLayers = listOf(
            VectorLayer("1"),
            VectorLayer("2", isVisible = false),
            VectorLayer("3"),
            VectorLayer("4")
        )

        LayerListPanel(
            currentLayers = sampleLayers,
            activeLayer = sampleLayers[3],
            onAddLayer = {},
            onSelectLayer = {},
            onToggleVisibility = {},
            onDeleteLayer = {}
        )

    }
}