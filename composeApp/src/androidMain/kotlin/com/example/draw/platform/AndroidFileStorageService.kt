package com.example.draw.platform

import android.content.Context
import com.example.draw.data.datasource.FileStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileStorageService(private val context: Context) : FileStorageService {

    // Lấy thư mục gốc ứng dụng
    private fun getFile(fileName: String): File {
        return File(context.filesDir, fileName)
    }

    override suspend fun saveFile(fileName: String, content: ByteArray) {
        return withContext(Dispatchers.IO) {
            getFile(fileName).writeBytes(content)
        }
    }

    override suspend fun readFile(fileName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val file = getFile(fileName)
            if (file.exists()) file.readBytes() else null
        }
    }

    override suspend fun deleteFile(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            getFile(fileName).delete()
        }
    }

    override suspend fun exists(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            getFile(fileName).exists()
        }
    }
}