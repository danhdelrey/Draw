package com.example.draw.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFileSaver(): FileSaver {
    val context = LocalContext.current
    val saver = remember { AndroidFileSaver(context) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { saver.onFileCreated(it) }
    }

    saver.launcher = launcher
    return saver
}

class AndroidFileSaver(private val context: Context) : FileSaver {
    var launcher: ManagedActivityResultLauncher<String, Uri?>? = null
    private var pendingContent: ByteArray? = null

    override fun save(fileName: String, content: ByteArray) {
        pendingContent = content
        launcher?.launch(fileName)
    }

    fun onFileCreated(uri: Uri) {
        pendingContent?.let { bytes ->
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(bytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingContent = null
            }
        }
    }
}

