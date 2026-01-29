package com.example.draw.ui.support_feature.drawingProject.create.mainComponent

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.platform.rememberFilePicker
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.support_feature.drawingProject.create.component.CreateDrawingProjectDialog
import kotlinx.serialization.json.Json

@Composable
fun CreateDrawingProjectButton(
    onCreateRequest: (CanvasConfig, String) -> Unit,
    onImportRequest: (DrawingProject) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val filePicker = rememberFilePicker()

    Box {
        CustomIconButton(
            icon = Icons.Default.Add
        ) {
            showMenu = true
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("New Project") },
                onClick = {
                    showMenu = false
                    showDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Import JSON") },
                onClick = {
                    showMenu = false
                    filePicker.pickFile(listOf("json")) { fileName, content ->
                         if (content != null) {
                             try {
                                 val jsonString = content.decodeToString()
                                 val project = Json.decodeFromString<DrawingProject>(jsonString)
                                 // Ensure project name is at most 20 characters
                                 val trimmedProject = project.copy(
                                     name = if (project.name.replace(" ", "").length > 20) project.name.replace(" ", "").take(20) else project.name.replace(" ", "")
                                 )
                                 onImportRequest(trimmedProject)
                             } catch (e: Exception) {
                                 e.printStackTrace()
                             }
                         }
                    }
                }
            )
        }
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