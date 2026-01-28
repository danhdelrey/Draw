package com.example.draw.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToURL
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
actual fun rememberFileSaver(): FileSaver {
    return remember { IosFileSaver() }
}

class IosFileSaver : FileSaver {
    @OptIn(ExperimentalForeignApi::class)
    override fun save(fileName: String, content: ByteArray) {
        val tempDir = NSTemporaryDirectory()
        val path = tempDir + fileName
        val url = NSURL.fileURLWithPath(path)

        val data = content.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), content.size.toULong())
        }

        data.writeToURL(url, true)

        val controller = UIActivityViewController(
            activityItems = listOf(url),
            applicationActivities = null
        )

        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController

        // controller.popoverPresentationController?.sourceView = rootController?.view

        rootController?.presentViewController(controller, animated = true, completion = null)
    }
}

