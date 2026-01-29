package com.example.draw.data.datasource.local

import com.example.draw.data.model.canvas.CanvasConfig
import com.example.draw.data.model.serialization.DrawingProject
import com.example.draw.data.model.util.generateId
import com.example.draw.data.service.FileStorageService
import kotlinx.serialization.json.Json

interface DrawingRepository {
    suspend fun getAllDrawingProjects(): List<DrawingProject>
    suspend fun saveDrawingProject(drawing: DrawingProject): Boolean
    suspend fun deleteDrawingProject(id: String): Boolean
    suspend fun createDrawingProject(projectName: String, canvasConfig: CanvasConfig): DrawingProject
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

    override suspend fun saveDrawingProject(drawing: DrawingProject): Boolean {
        try {
           val path = fileStorageService.saveFile(
                fileName = drawing.id,
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

    override suspend fun deleteDrawingProject(id: String): Boolean {
        return fileStorageService.deleteFile(id, "drawings")
    }

    override suspend fun createDrawingProject(projectName: String, canvasConfig: CanvasConfig): DrawingProject {
        val newId = generateId()
        val newProject = DrawingProject.defaultProject().copy(
            id = newId,
            name = projectName,
            width = canvasConfig.width,
            height = canvasConfig.height
        )

        val projectPath = fileStorageService.saveFile(
            fileName = newId,
            folderPath = "drawings",
            content = Json.encodeToString(newProject).encodeToByteArray()
        )
        println("Created new drawing project at path: $projectPath")
        return newProject
    }
}