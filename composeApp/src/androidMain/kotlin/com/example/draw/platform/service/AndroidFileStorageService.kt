package com.example.draw.platform.service

import android.content.Context
import com.example.draw.data.service.FileStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileStorageService(private val context: Context) : FileStorageService {

    private fun getFile(folderPath: String, fileName: String): File {
        val baseDir = File(context.filesDir, folderPath)
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }

        return File(baseDir, fileName)
    }


    override suspend fun saveFile(
        fileName: String,
        folderPath: String,
        content: ByteArray
    ): String = withContext(Dispatchers.IO) {

        println("folderPath = '$folderPath'")
        println("fileName   = '$fileName'")

        val file = getFile(folderPath, fileName)

        println("Saving file to: ${file.absolutePath}")

        file.writeBytes(content)

        file.absolutePath
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

    override suspend fun listFiles(folderPath: String): List<String> =
        withContext(Dispatchers.IO) {

            val directory = File(context.filesDir, folderPath)

            println("📂 Listing files in: ${directory.absolutePath}")
            println("   exists=${directory.exists()}, isDir=${directory.isDirectory}")

            if (!directory.exists() || !directory.isDirectory) {
                println("⚠️ Directory does not exist or is not a directory")
                return@withContext emptyList()
            }

            val files = directory.list()?.toList().orEmpty()

            println("📄 Found ${files.size} item(s):")
            files.forEach { println("   - $it") }

            files
        }

}