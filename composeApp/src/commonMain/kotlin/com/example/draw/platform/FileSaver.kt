package com.example.draw.platform

import androidx.compose.runtime.Composable

interface FileSaver {
    fun save(fileName: String, content: ByteArray)
}

@Composable
expect fun rememberFileSaver(): FileSaver

