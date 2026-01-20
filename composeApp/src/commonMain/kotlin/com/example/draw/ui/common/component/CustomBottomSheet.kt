package com.example.draw.ui.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.draw.ui.feature.pickColor.component.ColorGrid
import com.example.draw.ui.feature.pickColor.mockData.colorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp), // Hình gốc vuông góc trên cùng (tùy chọn) hoặc bo tròn
        containerColor = Color.White,
        dragHandle = null // Ẩn thanh kéo mặc định
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}