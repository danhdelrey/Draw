package com.example.draw.platform.service

import android.content.Context
import com.example.draw.data.service.FileStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileStorageService(private val context: Context) : FileStorageService {

    /**
     * Helper function để lấy đối tượng File.
     * folderPath sẽ là thư mục con bên trong internal storage của app.
     */
    private fun getFile(folderPath: String, fileName: String): File {
        // Tạo đường dẫn đến thư mục con: /data/data/com.app/files/folderPath
        val directory = File(context.filesDir, folderPath)

        // Tạo thư mục nếu chưa tồn tại (chỉ cần thiết khi ghi, nhưng an toàn khi gọi chung)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        return File(directory, fileName)
    }

    override suspend fun saveFile(fileName: String, folderPath: String, content: ByteArray) {
        withContext(Dispatchers.IO) {
            val file = getFile(folderPath, fileName)
            file.writeBytes(content)
        }
    }

    override suspend fun readFile(fileName: String, folderPath: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val file = getFile(folderPath, fileName)
            if (file.exists()) file.readBytes() else null
        }
    }

    override suspend fun deleteFile(fileName: String, folderPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = getFile(folderPath, fileName)
            file.delete()
        }
    }

    override suspend fun exists(fileName: String, folderPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = getFile(folderPath, fileName)
            file.exists()
        }
    }
}