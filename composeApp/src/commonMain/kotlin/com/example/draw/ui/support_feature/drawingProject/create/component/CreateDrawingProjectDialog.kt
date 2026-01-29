package com.example.draw.ui.support_feature.drawingProject.create.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.ui.common.preview.PreviewComponent
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.ui.common.component.dialog.AppBaseDialog

@Composable
fun CreateDrawingProjectDialog(
    onDismissRequest: () -> Unit,
    onCreateRequest: (CanvasConfig, String) -> Unit
) {
    // State management
    var projectName by remember { mutableStateOf("Untitled Project") }
    var widthText by remember { mutableStateOf(CanvasConfig.DEFAULT_WIDTH.toInt().toString()) }
    var heightText by remember { mutableStateOf(CanvasConfig.DEFAULT_HEIGHT.toInt().toString()) }

    // Quản lý focus để UX mượt mà (Nhấn Next tự nhảy xuống ô dưới)
    val focusManager = LocalFocusManager.current

    // Validation Logic
    val isValidInput by remember {
        derivedStateOf {
            projectName.isNotBlank() &&
                    projectName.length <= 20 &&
                    (widthText.toIntOrNull() ?: 0) >= 100 &&
                    (heightText.toIntOrNull() ?: 0) >= 100
        }
    }

    AppBaseDialog(
        title = "New Project",
        icon = Icons.Default.Create, // Có thể thay bằng icon trong resource
        confirmText = "Create",
        dismissText = "Cancel",
        onDismissRequest = onDismissRequest,
        isConfirmEnabled = isValidInput,
        onConfirm = {
            val width = widthText.toFloatOrNull() ?: CanvasConfig.DEFAULT_WIDTH
            val height = heightText.toFloatOrNull() ?: CanvasConfig.DEFAULT_HEIGHT
            val config = CanvasConfig(width = width, height = height)
            onCreateRequest(config, projectName)
        }
    ) {
        // --- Content của Dialog ---

        // 1. Project Name Input
        OutlinedTextField(
            value = projectName,
            onValueChange = {
                if (it.length <= 20) projectName = it
            },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            supportingText = {
                if (projectName.length > 20) {
                    Text("Project name must be 20 characters or less", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // 2. Width & Height Inputs (Nằm cùng 1 hàng)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Width
            OutlinedTextField(
                value = widthText,
                onValueChange = { if (it.all { char -> char.isDigit() }) widthText = it },
                label = { Text("Width") },
                suffix = { Text("px", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                supportingText = {
                    val value = widthText.toIntOrNull() ?: 0
                    if (value < 100) {
                        Text("Min: 100", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Height
            OutlinedTextField(
                value = heightText,
                onValueChange = { if (it.all { char -> char.isDigit() }) heightText = it },
                label = { Text("Height") },
                suffix = { Text("px", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    // Ẩn bàn phím khi xong
                    focusManager.clearFocus()
                }),
                supportingText = {
                    val value = heightText.toIntOrNull() ?: 0
                    if (value < 100) {
                        Text("Min: 100", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun CreateDrawingProjectDialogPreview() {
    PreviewComponent {
        CreateDrawingProjectDialog(
            onDismissRequest = {},
            onCreateRequest = { _, _ -> }
        )
    }
}