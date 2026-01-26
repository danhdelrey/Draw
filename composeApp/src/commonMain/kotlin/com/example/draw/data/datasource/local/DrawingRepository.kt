package com.example.draw.data.datasource.local

import com.example.draw.data.datasource.model.DrawingData
import com.example.draw.data.service.FileStorageService

interface DrawingRepository {
    suspend fun getAllDrawings(): List<DrawingData>
    suspend fun getDrawingById(id: String): DrawingData?
    suspend fun saveDrawing(drawing: DrawingData): Boolean
    suspend fun deleteDrawing(id: String): Boolean
}

class DrawingRepositoryImpl(
    private val fileStorageService: FileStorageService
) : DrawingRepository {
    override suspend fun getAllDrawings(): List<DrawingData> {
        val files = fileStorageService.listFiles("drawings")
        return files.mapNotNull { fileName ->
            val data = fileStorageService.readFile(fileName, "drawings")
            data?.let { fileData ->
                null
            }
        }
    }

    override suspend fun getDrawingById(id: String): DrawingData? {
        TODO("Not yet implemented")
    }

    override suspend fun saveDrawing(drawing: DrawingData): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDrawing(id: String): Boolean {
        TODO("Not yet implemented")
    }
}