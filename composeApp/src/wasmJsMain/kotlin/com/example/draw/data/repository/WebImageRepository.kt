package com.example.draw.data.repository

import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

class WebImageRepository : ImageRepository {
    @OptIn(ExperimentalWasmJsInterop::class)
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return try {
            val jsData = Int8Array(imageData.size)
            for (i in imageData.indices) {
                jsData[i] = imageData[i]
            }

            val blobParts = JsArray<JsAny?>()
            blobParts[0] = jsData

            val blob = Blob(blobParts, BlobPropertyBag(type = "image/png"))
            val url = URL.createObjectURL(blob)

            val link = document.createElement("a") as HTMLAnchorElement
            link.href = url
            link.download = "$fileName.png"
            document.body?.appendChild(link)
            link.click()
            document.body?.removeChild(link)

            true
        } catch (e: Exception) {
            false
        }
    }
}