package com.example.draw

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import com.example.draw.ui.feature.drawing.view.DrawingScreen
import com.example.draw.ui.feature.gallery.view.GalleryScreen

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Navigator(GalleryScreen()){ navigator ->

            AnimatedContent(
                targetState = navigator.lastItem,
                transitionSpec = {
                    if (navigator.lastEvent == StackEvent.Pop) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    }
                }
            ) { screen ->
                screen.Content()
            }
        }
    }
}