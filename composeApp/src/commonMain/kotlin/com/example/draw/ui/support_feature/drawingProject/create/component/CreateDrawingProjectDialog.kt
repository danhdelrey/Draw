package com.example.draw.ui.support_feature.drawingProject.create.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun CreateDrawingProjectDialog(
    onDismissRequest: () -> Unit,
    onCreateRequest: (CanvasConfig,String) -> Unit
) {
    var projectName by remember { mutableStateOf("projectA") }
    var widthText by remember { mutableStateOf(CanvasConfig.DEFAULT_WIDTH.toInt().toString()) }
    var heightText by remember { mutableStateOf(CanvasConfig.DEFAULT_HEIGHT.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Create New Project") },
        text = {
            Column {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = widthText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) widthText = it },
                    label = { Text("Width") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) heightText = it },
                    label = { Text("Height") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val width = widthText.toFloatOrNull() ?: CanvasConfig.DEFAULT_WIDTH
                    val height = heightText.toFloatOrNull() ?: CanvasConfig.DEFAULT_HEIGHT
                    val config = CanvasConfig(width = width, height = height)
                    onCreateRequest(config, projectName)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
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