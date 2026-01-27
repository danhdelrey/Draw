package com.example.draw.ui.feature.gallery.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.feature.drawing.component.DrawingCanvas
import com.example.draw.data.model.layer.VectorLayer
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.view.DrawingScreen
import com.example.draw.ui.feature.drawing.viewModel.DrawingState
import com.example.draw.ui.feature.gallery.viewModel.GalleryEffect
import com.example.draw.ui.feature.gallery.viewModel.GalleryEvent
import com.example.draw.ui.feature.gallery.viewModel.GalleryScreenViewModel
import com.example.draw.ui.support_feature.drawingProject.create.mainComponent.CreateDrawingProjectButton

class GalleryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<GalleryScreenViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
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
                            Card(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    navigator?.push(DrawingScreen(initState = DrawingState.fromDrawingProject(project)))
                                }
                            ) {
                                Column {
                                    DrawingProjectThumbnail(
                                        project = project,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                    )
                                    Text(
                                        text = project.name,
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawingProjectThumbnail(
    project: DrawingProject,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val drawingState = remember(project) { DrawingState.fromDrawingProject(project) }

        val canvasWidth = project.width
        val canvasHeight = project.height

        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        val scale = minOf(
            availableWidth / canvasWidth,
            availableHeight / canvasHeight
        )

        val displayedWidth = canvasWidth * scale
        val displayedHeight = canvasHeight * scale

        Box(
            modifier = Modifier
                .size(
                    width = with(density) { displayedWidth.toDp() },
                    height = with(density) { displayedHeight.toDp() }
                )
                .background(Color(project.backgroundColor))
        ) {
            drawingState.layers.forEach { layer ->
                if (layer.isVisible && layer is VectorLayer) {
                    DrawingCanvas(
                        paths = layer.paths,
                        currentPath = null,
                        isEraserMode = false,
                        currentTouchPosition = null,
                        brushSize = 0f,
                        renderScale = scale,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GalleryScreenPreview() {
    PreviewComponent {
        GalleryScreen().Content()
    }
}
