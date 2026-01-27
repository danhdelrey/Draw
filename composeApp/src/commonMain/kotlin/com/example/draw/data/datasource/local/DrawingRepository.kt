package com.example.draw.data.datasource.local

import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.service.FileStorageService

interface DrawingRepository {
    suspend fun getAllDrawingProjects(): List<DrawingProject>
    suspend fun getDrawingProjectById(id: String): DrawingProject?
    suspend fun saveDrawingProject(drawing: DrawingProject): Boolean
    suspend fun deleteDrawingProject(id: String): Boolean
}

class DrawingRepositoryImpl(
    private val fileStorageService: FileStorageService
) : DrawingRepository {
    override suspend fun getAllDrawingProjects(): List<DrawingProject> {
        val files = fileStorageService.listFiles("drawings")
        return files.mapNotNull { fileName ->
            val data = fileStorageService.readFile(fileName, "drawings")
            data?.let { fileData ->
                null
            }
        }
    }

    override suspend fun getDrawingProjectById(id: String): DrawingProject? {
        TODO("Not yet implemented")
    }

    override suspend fun saveDrawingProject(drawing: DrawingProject): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDrawingProject(id: String): Boolean {
        TODO("Not yet implemented")
    }
}