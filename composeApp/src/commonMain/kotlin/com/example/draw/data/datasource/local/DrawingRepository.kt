package com.example.draw.data.datasource.local

import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.service.FileStorageService
import kotlinx.serialization.json.Json

interface DrawingRepository {
    suspend fun getAllDrawingProjects(): List<DrawingProject>
    suspend fun getDrawingProjectByName(name: String): DrawingProject?
    suspend fun saveDrawingProject(drawing: DrawingProject): Boolean
    suspend fun deleteDrawingProject(name: String): Boolean
}

class DrawingRepositoryImpl(
    private val fileStorageService: FileStorageService
) : DrawingRepository {
    override suspend fun getAllDrawingProjects(): List<DrawingProject> {
        val files = fileStorageService.listFiles("drawings")
        return files.mapNotNull { fileName ->
            val data = fileStorageService.readFile(fileName, "drawings")
            data?.let { fileData ->
                Json.decodeFromString<DrawingProject>(fileData.decodeToString())
            }
        }
    }

    override suspend fun getDrawingProjectByName(name: String): DrawingProject? {
        val project = fileStorageService.readFile(name, "drawings")
        return project?.let { data ->
            Json.decodeFromString<DrawingProject>(data.decodeToString())
        }
    }

    override suspend fun saveDrawingProject(drawing: DrawingProject): Boolean {
        try {
           val path = fileStorageService.saveFile(
                fileName = drawing.name,
                folderPath = "drawings",
                content = Json.encodeToString(drawing).encodeToByteArray()
            )
            println("Saved drawing at path: $path")
            return path != null
        }catch (e : Exception){
            println("Error saving drawing: ${e.message}")
            return false
        }

    }

    override suspend fun deleteDrawingProject(name: String): Boolean {
        return fileStorageService.deleteFile(name, "drawings")
    }
}