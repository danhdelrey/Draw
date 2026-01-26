package com.example.draw.data.datasource.local

import com.example.draw.data.datasource.model.DrawingData

interface DrawingRepository {
    suspend fun getAllDrawings(): List<DrawingData>
    suspend fun getDrawingById(id: String): DrawingData?
    suspend fun saveDrawing(drawing: DrawingData): Boolean
    suspend fun deleteDrawing(id: String): Boolean
}

class DrawingRepositoryImpl : DrawingRepository {
    override suspend fun getAllDrawings(): List<DrawingData> {
        TODO("Not yet implemented")
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