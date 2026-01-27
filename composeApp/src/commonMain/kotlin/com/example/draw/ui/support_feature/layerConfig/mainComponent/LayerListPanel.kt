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
    canvasWidth: Float,
    canvasHeight: Float,
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

        // 2. Danh sách Layer (LazyColumn)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true // Đảo ngược danh sách (Layer mới nhất ở trên)
        ) {
            items(currentLayers) { layer ->
                LayerItem(
                    data = layer,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
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
    val layer1 = VectorLayer(id = "1", name = "Layer 1")
    val layer2 = VectorLayer(id = "2", name = "Layer 2", isVisible = false)

    PreviewComponent {
        LayerListPanel(
            currentLayers = listOf(layer1, layer2),
            activeLayer = layer1,
            canvasWidth = 100f,
            canvasHeight = 100f,
            onAddLayer = {},
            onSelectLayer = {},
            onToggleVisibility = {},
            onDeleteLayer = {}
        )

    }
}