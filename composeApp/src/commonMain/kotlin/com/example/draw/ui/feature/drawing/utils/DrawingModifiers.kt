package com.example.draw.ui.feature.drawing.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize

fun Modifier.drawingInput(
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
): Modifier {
    return this.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                // Tự động kẹp tọa độ vào trong size của canvas
                onDragStart(offset.clampToSize(size.toSize()))
            },
            onDrag = { change, _ ->
                // Tự động kẹp tọa độ
                onDrag(change.position.clampToSize(size.toSize()))
            },
            onDragEnd = { onDragEnd() }
        )
    }
}