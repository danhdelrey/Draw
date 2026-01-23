package com.example.draw.ui.feature.gallery.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.drawing.view.DrawingScreen

class GalleryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Gallery")
                    }
                )
            },
            floatingActionButton = {
                CustomIconButton(
                    icon = Icons.Default.Add
                ){
                    navigator?.push(DrawingScreen())
                }
            }
        ) {

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