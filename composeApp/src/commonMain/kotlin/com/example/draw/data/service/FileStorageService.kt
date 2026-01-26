package com.example.draw.data.service

interface FileStorageService {
    // Lưu file (Ghi đè nếu đã tồn tại - đóng vai trò là Sửa)
    suspend fun saveFile(fileName: String, content: ByteArray)

    // Đọc file
    suspend fun readFile(fileName: String): ByteArray?

    // Xóa file
    suspend fun deleteFile(fileName: String): Boolean

    // Kiểm tra file tồn tại
    suspend fun exists(fileName: String): Boolean
}