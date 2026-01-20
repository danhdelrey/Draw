package com.example.draw.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreviewBackground(content: @Composable () -> Unit) {
    MaterialTheme {
        Scaffold(
            modifier = Modifier.width(412.dp).height(800.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PreviewComponent(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}