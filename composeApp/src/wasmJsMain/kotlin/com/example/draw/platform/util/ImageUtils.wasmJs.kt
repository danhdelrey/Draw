package com.example.draw.platform.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // Convert to Skia Bitmap
    val skiaBitmap = this.asSkiaBitmap()
    // Create Image from Bitmap and encode to PNG
    val image = Image.makeFromBitmap(skiaBitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}