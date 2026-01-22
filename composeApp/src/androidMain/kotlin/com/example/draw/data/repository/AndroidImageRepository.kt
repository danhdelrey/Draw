package com.example.draw.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidImageRepository(private val context: Context) : ImageRepository {
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DrawApp")
                    }
                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        ?: return@withContext false

                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(imageData)
                    }
                    true
                } else {
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val file = File(picturesDir, "$fileName.png")
                    FileOutputStream(file).use { outputStream ->
                        outputStream.write(imageData)
                    }
                    true
                }
            } catch (e: Exception) {
                false
            }
        }
    }
}