package com.example.draw.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.darwin.NSObject
import platform.UniformTypeIdentifiers.UTTypeJSON
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes

@Composable
actual fun rememberFilePicker(): FilePicker {
    return remember { IosFilePicker() }
}

class IosFilePicker : FilePicker {
    private val delegate = PickerDelegate()

    override fun pickFile(allowedExtensions: List<String>, onResult: (String?, ByteArray?) -> Unit) {
        delegate.onResult = onResult

        val types = allowedExtensions.mapNotNull { ext ->
            when(ext) {
                "json" -> UTTypeJSON
                else -> null
            }
        }

        val picker = UIDocumentPickerViewController(forOpeningContentTypes = types, asCopy = true)
        picker.delegate = delegate

        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootController?.presentViewController(picker, animated = true, completion = null)
    }
}

class PickerDelegate : NSObject(), UIDocumentPickerDelegateProtocol {
    var onResult: ((String?, ByteArray?) -> Unit)? = null

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return

        val data = NSData.dataWithContentsOfURL(url)
        val bytes = data?.run {
             @OptIn(ExperimentalForeignApi::class)
             this.bytes?.readBytes(this.length.toInt())
        }
        val fileName = url.lastPathComponent

        onResult?.invoke(fileName, bytes)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        // Do nothing
    }
}

