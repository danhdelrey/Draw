package com.example.draw.data.repository

interface ImageRepository {
    suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean
}