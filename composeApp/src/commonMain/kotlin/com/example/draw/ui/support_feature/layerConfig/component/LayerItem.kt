package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun LayerItem(
    data: Layer,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleVisibility: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF888888) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Nút Mắt
        IconButton(
            onClick = onToggleVisibility,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (data.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                tint = Color(0xFF1E1E1E)
            )
        }

        // 2. Khung Preview (Thumbnail)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
                // QUAN TRỌNG: Set tỉ lệ khung hình dựa trên kích thước cố định
                .aspectRatio(CanvasConfig.FIXED_WIDTH / CanvasConfig.FIXED_HEIGHT)
                .background(Color.Gray) // Màu nền của vùng chứa (để phân biệt với giấy)
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            // Vẽ nền giấy trắng
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )

            // Vẽ nội dung layer lên trên
            if (data is VectorLayer) {
                LayerThumbnail(
                    layer = data,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 3. Nút Xóa
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Layer",
                tint = Color(0xFF1E1E1E)
            )
        }
    }
}

@Preview
@Composable
fun LayerItemPreview() {
    PreviewComponent {
        LayerItem(
            data = VectorLayer(id = "1", isVisible = true),
            isSelected = true,
            onClick = {},
            onToggleVisibility = {},
            onDelete = {}
        )
    }
}