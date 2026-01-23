package com.example.draw.ui.feature.gallery.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.feature.drawing.view.DrawingScreen

class GalleryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Scaffold(
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