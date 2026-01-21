package com.example.draw.ui.support_feature.layerConfig.component

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.layerConfig.model.LayerConfig

@Composable
fun LayerListPanel(
    onAddLayer: () -> Unit,
    onSelectLayer: (Int) -> Unit,
    onToggleVisibility: (Int) -> Unit,
    onDeleteLayer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val sampleLayers = remember {
        mutableStateListOf(
            LayerConfig(id = 1, isVisible = true),
            LayerConfig(id = 2, isVisible = false),
            LayerConfig(id = 3, isVisible = true),
            LayerConfig(id = 4, isVisible = true),
            LayerConfig(id = 5, isVisible = true),
        )
    }


    var selectedLayerId by remember { mutableStateOf(2) }


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
                onClick = {
                    onAddLayer()
                    sampleLayers.add(
                        LayerConfig(
                            id = sampleLayers.last().id + 1,
                            isVisible = true
                        )
                    )

                },
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
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(
                items = sampleLayers,
                key = {it.id}
            ) { layer ->
                LayerItem(
                    data = layer,
                    isSelected = layer.id == selectedLayerId,
                    onClick = {
                        onSelectLayer(layer.id)
                        selectedLayerId = layer.id

                    },
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
            onAddLayer = {},
            onSelectLayer = {},
            onToggleVisibility = {},
            onDeleteLayer = {}
        )
    }
}