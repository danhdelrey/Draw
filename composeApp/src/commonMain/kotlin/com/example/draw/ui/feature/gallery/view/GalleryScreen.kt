package com.example.draw.ui.feature.gallery.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.ui.common.component.CanvasThumbnail
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.view.DrawingScreen
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import com.example.draw.ui.feature.gallery.viewModel.GalleryEffect
import com.example.draw.ui.feature.gallery.viewModel.GalleryEvent
import com.example.draw.ui.feature.gallery.viewModel.GalleryScreenViewModel
import com.example.draw.ui.support_feature.drawingProject.create.mainComponent.CreateDrawingProjectButton
import androidx.compose.ui.tooling.preview.Preview

class GalleryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<GalleryScreenViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        var showMenuForProject by remember { mutableStateOf<DrawingProject?>(null) }
        var showRenameForProject by remember { mutableStateOf<DrawingProject?>(null) }
        var showDeleteForProject by remember { mutableStateOf<DrawingProject?>(null) }

        LaunchedEffect(Unit) {
            viewModel.onEvent(GalleryEvent.LoadDrawingProjects)
            viewModel.effect.collect { effect ->
                when (effect) {
                    is GalleryEffect.CreateDrawingProjectSuccess -> {
                        navigator?.push(DrawingScreen(initState = effect.drawingState))
                    }

                }
            }
        }


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Gallery")
                    }
                )
            },
            floatingActionButton = {
                CreateDrawingProjectButton { canvasConfig , projectName ->
                    viewModel.onEvent(GalleryEvent.CreateDrawingProject(canvasConfig, projectName))
                }
            }
        ) { paddingValues ->
            when (state.isLoading) {
                true -> {
                    Box(
                        modifier = Modifier.padding(paddingValues).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                false -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.padding(paddingValues).fillMaxSize()
                    ) {
                        items(state.drawingProjects) { project ->
                            Box {
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .combinedClickable(
                                            onClick = {
                                                navigator?.push(DrawingScreen(initState = DrawingState.fromDrawingProject(project)))
                                            },
                                            onLongClick = {
                                                showMenuForProject = project
                                            }
                                        )
                                ) {
                                    Column {
                                        DrawingProjectThumbnail(
                                            project = project,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                        )
                                        Text(
                                            text = project.name.removeSuffix(".json"),
                                            modifier = Modifier.padding(8.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showMenuForProject == project,
                                    onDismissRequest = { showMenuForProject = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename") },
                                        onClick = {
                                            showRenameForProject = project
                                            showMenuForProject = null
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            showDeleteForProject = project
                                            showMenuForProject = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRenameForProject != null) {
            RenameProjectDialog(
                project = showRenameForProject!!,
                onDismiss = { showRenameForProject = null },
                onConfirm = { newName ->
                    viewModel.onEvent(GalleryEvent.RenameDrawingProject(showRenameForProject!!, newName))
                    showRenameForProject = null
                }
            )
        }

        if (showDeleteForProject != null) {
            DeleteProjectDialog(
                project = showDeleteForProject!!,
                onDismiss = { showDeleteForProject = null },
                onConfirm = {
                    viewModel.onEvent(GalleryEvent.DeleteDrawingProject(showDeleteForProject!!.name))
                    showDeleteForProject = null
                }
            )
        }
    }
}

@Composable
fun RenameProjectDialog(
    project: DrawingProject,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(project.name.removeSuffix(".json")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Project") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Project Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (newName.endsWith(".json")) newName else "$newName.json"
                    onConfirm(finalName)
                }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteProjectDialog(
    project: DrawingProject,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Project") },
        text = { Text("Are you sure you want to delete '${project.name}'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DrawingProjectThumbnail(
    project: DrawingProject,
    modifier: Modifier = Modifier
) {
    val drawingState = remember(project) { DrawingState.fromDrawingProject(project) }

    CanvasThumbnail(
        layers = drawingState.layers.filterIsInstance<VectorLayer>(),
        canvasWidth = project.width,
        canvasHeight = project.height,
        backgroundColor = Color(project.backgroundColor),
        modifier = modifier.fillMaxSize()
    )
}

@Preview
@Composable
fun GalleryScreenPreview() {
    PreviewComponent {
        GalleryScreen().Content()
    }
}
