package com.example.draw.platform.service

import com.example.draw.data.service.FileStorageService
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import platform.Foundation.writeToURL

class IosFileStorageService : FileStorageService {

    private val fileManager = NSFileManager.Companion.defaultManager

    @OptIn(ExperimentalForeignApi::class)
    private fun getDocumentDirectory(): NSURL? {
        return fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
    }

    private fun getFileUrl(fileName: String): NSURL? {
        return getDocumentDirectory()?.URLByAppendingPathComponent(fileName)
    }

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    override suspend fun saveFile(fileName: String, content: ByteArray) {
        withContext(Dispatchers.IO) {
            val fileUrl = getFileUrl(fileName) ?: return@withContext

            // Chuyển ByteArray sang NSData
            val data = content.usePinned { pinned ->
                NSData.Companion.dataWithBytes(pinned.addressOf(0), content.size.toULong())
            }
            data.writeToURL(fileUrl, true)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun readFile(fileName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val fileUrl = getFileUrl(fileName) ?: return@withContext null
            val data = NSData.Companion.dataWithContentsOfURL(fileUrl) ?: return@withContext null

            val byteArray = ByteArray(data.length.toInt())
            byteArray.usePinned { pinned ->
                data.getBytes(pinned.addressOf(0), data.length)
            }
            byteArray
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deleteFile(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val fileUrl = getFileUrl(fileName) ?: return@withContext false
            try {
                fileManager.removeItemAtURL(fileUrl, null)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun exists(fileName: String): Boolean {
        val fileUrl = getFileUrl(fileName) ?: return false
        return fileManager.fileExistsAtPath(fileUrl.path ?: "")
    }
}