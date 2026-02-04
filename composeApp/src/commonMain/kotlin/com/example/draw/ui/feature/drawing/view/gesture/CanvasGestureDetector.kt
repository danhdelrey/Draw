package com.example.draw.ui.feature.drawing.view.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectCanvasGestures(
    shouldIntercept: (pointerCount: Int) -> Boolean,
    onGestureStart: (centroid: Offset) -> Unit,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
    onGestureEnd: () -> Unit = {}
) {
    awaitEachGesture {
        var transformStarted = false
        // Use PointerEventPass.Initial to observe events BEFORE the child (DrawingCanvas)
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

        do {
            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
            val pointerCount = event.changes.size

            if (shouldIntercept(pointerCount)) {
                // Intercept! Consume all changes so child sees them as consumed in Main pass.
                event.changes.forEach {
                    it.consume()
                }

                val centroid = event.calculateCentroid(useCurrent = false)
                val pan = event.calculatePan()
                val zoomChangeStep = event.calculateZoom()
                val rotationChangeStep = event.calculateRotation()

                if (!transformStarted) {
                    transformStarted = true
                    onGestureStart(centroid)
                }

                if (zoomChangeStep != 1f || rotationChangeStep != 0f || pan != Offset.Zero) {
                    onGesture(centroid, pan, zoomChangeStep, rotationChangeStep)
                }
            } else {
                if (transformStarted) {
                    transformStarted = false
                    onGestureEnd()
                }
            }

        } while (event.changes.any { it.pressed })

        if (transformStarted) {
             onGestureEnd()
        }
    }
}

