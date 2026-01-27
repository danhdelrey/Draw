package com.example.draw.ui.feature.gallery.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
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
                                Box(
                                    modifier = Modifier.padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Display project details, e.g., name or thumbnail
                                    // Assuming drawingProjects has a 'name' property
                                    Text(text = project.name)
                                }
                            }
                        }
                    }
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
