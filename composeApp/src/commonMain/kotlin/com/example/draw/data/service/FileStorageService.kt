package com.example.draw.data.service

interface FileStorageService {
    suspend fun saveFile(fileName: String, folderPath: String, content: ByteArray)
    suspend fun readFile(fileName: String,  folderPath: String): ByteArray?
    suspend fun deleteFile(fileName: String,  folderPath: String): Boolean
    suspend fun exists(fileName: String,  folderPath: String): Boolean
}