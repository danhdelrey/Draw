package com.example.draw.ui.support_feature.layerConfig.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.layerConfig.model.LayerConfig

@Composable
fun LayerItem(
    data: LayerConfig,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleVisibility: () -> Unit,
    onDelete: () -> Unit
) {
    // Màu nền thay đổi dựa trên trạng thái select (Giống item thứ 3 trong hình)
    val backgroundColor = if (isSelected) Color(0xFF888888) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Chiều cao mỗi hàng
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Căn đều 3 phần tử
    ) {
        // 1. Nút Mắt (Trái)
        IconButton(
            onClick = onToggleVisibility,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (data.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                tint = Color(0xFF1E1E1E) // Màu đen nhạt
            )
        }

        // 2. Khung trắng (Giữa) - Placeholder nội dung
        Box(
            modifier = Modifier
                .weight(1f) // Chiếm phần không gian còn lại ở giữa
                .padding(horizontal = 4.dp)
                .aspectRatio(0.8f) // Tỉ lệ khung hình chữ nhật đứng
                .background(Color.White)
        ) {
            // Sau này bạn có thể để Image() ở đây
        }

        // 3. Nút Thùng rác (Phải)
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
            data = LayerConfig(id = 1, isVisible = true),
            isSelected = true,
            onClick = {},
            onToggleVisibility = {},
            onDelete = {}
        )
    }
}