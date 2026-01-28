package com.example.draw.platform

import androidx.compose.runtime.Composable

interface FilePicker {
    fun pickFile(allowedExtensions: List<String>, onResult: (String?, ByteArray?) -> Unit)
}

@Composable
expect fun rememberFilePicker(): FilePicker

