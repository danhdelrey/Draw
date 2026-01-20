package com.example.draw.ui.screen.drawing.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import com.example.draw.ui.common.preview.PreviewWithScaffold

class DrawingScreen : Screen {
    @Composable
    override fun Content() {
        Text("hello")
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    PreviewWithScaffold { DrawingScreen().Content() }
}
