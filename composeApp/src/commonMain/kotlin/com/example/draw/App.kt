package com.example.draw

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.example.draw.ui.screen.drawing.view.DrawingScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(DrawingScreen())
    }
}