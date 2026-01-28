package com.example.draw.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberFilePicker(): FilePicker {
    val context = LocalContext.current
    val picker = remember { AndroidFilePicker(context) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { picker.onFilePicked(it) }
    }

    picker.launcher = launcher
    return picker
}

class AndroidFilePicker(private val context: Context) : FilePicker {
    var launcher: ManagedActivityResultLauncher<Array<String>, Uri?>? = null
    var onResult: ((String?, ByteArray?) -> Unit)? = null

    override fun pickFile(allowedExtensions: List<String>, onResult: (String?, ByteArray?) -> Unit) {
        this.onResult = onResult
        val mimeTypes = allowedExtensions.map { 
             when(it) {
                 "json" -> "application/json"
                 else -> "*/*"
             }
        }.toTypedArray()
        
        launcher?.launch(mimeTypes)
    }

    fun onFilePicked(uri: Uri) {
        try {
            val contentResolver = context.contentResolver
            
            var fileName: String? = null
            // Simple query for name, ignoring complex columns check for brevity as standard column names usually work or return null
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayNameIndex = cursor.getColumnIndex("_display_name")
                        if (displayNameIndex != -1) {
                            fileName = cursor.getString(displayNameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore name query failure
            }

            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.use { 
                val buffer = ByteArrayOutputStream()
                val data = ByteArray(1024)
                var nRead: Int
                while (it.read(data, 0, data.size).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.toByteArray()
            }
            onResult?.invoke(fileName, bytes)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult?.invoke(null, null)
        }
    }
}

