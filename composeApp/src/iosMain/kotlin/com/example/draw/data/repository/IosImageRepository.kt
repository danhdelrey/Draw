package com.example.draw.data.repository

class IosImageRepository : ImageRepository {
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return true
    }
}