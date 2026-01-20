package com.example.draw.ui.feature.drawing.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.draw.ui.preview.PreviewBackground

class DrawingScreen : Screen {
    @Composable
    override fun Content() {
        Text("hello")
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    PreviewBackground { DrawingScreen().Content() }
}
