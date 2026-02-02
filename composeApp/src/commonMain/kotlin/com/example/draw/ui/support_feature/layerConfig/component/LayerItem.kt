package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.layer.Layer
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun LayerItem(
    data: Layer,
    canvasWidth: Float,
    canvasHeight: Float,
    isSelected: Boolean,
    onClick: (() -> Unit)?,
    onToggleVisibility: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    showTransparentBackground: Boolean = false // New parameter
) {
    val backgroundColor = if (isSelected) Color(0xFF888888) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Chiều cao cố định cho item
            .background(backgroundColor)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(onToggleVisibility == null) {
            // Nếu không có onToggleVisibility, giữ khoảng trống cho nút mắt
            Box(modifier = Modifier.size(24.dp))
        }else{
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
        }


        // 2. Vùng chứa Thumbnail
        // Box này đóng vai trò là container giới hạn
        Box(
            modifier = Modifier
                .weight(1f) // Chiếm phần giữa
                .padding(horizontal = 8.dp)
                .fillMaxSize(), // Cao bằng Row, Rộng theo weight
            contentAlignment = Alignment.Center
        ) {
            if (data is VectorLayer) {
                // Gọi Thumbnail, nó sẽ tự căn giữa và giữ đúng tỷ lệ
                LayerThumbnail(
                    layer = data,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    showTransparentBackground = showTransparentBackground, // Pass it down
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback nếu không phải VectorLayer (ví dụ ImageLayer)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }
        }

        // 3. Nút Xóa
        if (onDelete != null) {
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
        } else {
            Box(modifier = Modifier.size(24.dp))
        }
    }
}
@Preview
@Composable
fun LayerItemPreview() {
    PreviewComponent {
        LayerItem(
            data = VectorLayer(id = "1", isVisible = true),
            canvasWidth = 100f,
            canvasHeight = 100f,
            isSelected = true,
            onClick = {},
            onToggleVisibility = {},
            onDelete = {}
        )
    }
}