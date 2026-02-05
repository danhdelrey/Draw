package com.example.draw.ui.common.component.toolPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.component.button.ElegantButton
import com.example.draw.ui.feature.drawing.viewModel.DrawingEvent

@Composable
fun BasicActionToolPanel(
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    shouldShowToolPanel: Boolean = false,
) {
    ToolPanel(
        shouldHideToolPanel = !shouldShowToolPanel,
    ){
        ElegantButton(
            icon = Icons.Default.Cancel,
            onClick = onCancel
        )
        ElegantButton(
            icon = Icons.Default.Check,
            onClick = onConfirm
        )

    }
}