package com.example.draw.ui.feature.layerConfig.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.layerConfig.component.LayerItem
import com.example.draw.ui.feature.layerConfig.model.LayerConfig

@Composable
fun LayerListPanel(
    layers: List<LayerConfig>,
    selectedLayerId: Int?,
    onAddLayer: () -> Unit,
    onSelectLayer: (Int) -> Unit,
    onToggleVisibility: (Int) -> Unit,
    onDeleteLayer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp) // Độ rộng ước lượng theo tỉ lệ hình ảnh
            .height(500.dp)
            .background(Color(0xFF555555)) // Màu nền xám đậm tổng thể
    ) {
        // --- Header với nút Add (+) ---
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
                    .background(Color(0xFF6750A4), CircleShape) // Màu tím Material 3
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Layer",
                    tint = Color.White
                )
            }
        }

        // --- Danh sách các Layer ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(layers) { layer ->
                LayerItem(
                    data = layer,
                    isSelected = layer.id == selectedLayerId,
                    onClick = { onSelectLayer(layer.id) },
                    onToggleVisibility = { onToggleVisibility(layer.id) },
                    onDelete = { onDeleteLayer(layer.id) }
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