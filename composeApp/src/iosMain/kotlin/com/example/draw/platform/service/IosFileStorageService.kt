package com.example.draw.platform.service

import com.example.draw.data.service.FileStorageService
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
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
    override suspend fun saveFile(
        fileName: String,
        folderPath: String,
        content: ByteArray
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readFile(
        fileName: String,
        folderPath: String
    ): ByteArray? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(
        fileName: String,
        folderPath: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun exists(
        fileName: String,
        folderPath: String
    ): Boolean {
        TODO("Not yet implemented")
    }

}