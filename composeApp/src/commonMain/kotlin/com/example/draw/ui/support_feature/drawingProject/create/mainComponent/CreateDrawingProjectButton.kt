package com.example.draw.ui.support_feature.drawingProject.create.mainComponent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.support_feature.drawingProject.create.component.CreateDrawingProjectDialog

@Composable
fun CreateDrawingProjectButton(
    onCreateRequest: (CanvasConfig, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    CustomIconButton(
        icon = Icons.Default.Add
    ) {
        showDialog = true
    }

    if (showDialog) {
        CreateDrawingProjectDialog(
            onDismissRequest = { showDialog = false },
            onCreateRequest = { canvasConfig, projectName ->
                onCreateRequest(canvasConfig, projectName)
                showDialog = false
            }
        )
    }
}