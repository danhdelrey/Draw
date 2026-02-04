package com.example.draw.ui.feature.drawing.view.gesture

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Offset.rotateBy(angleDegrees: Float): Offset {
    val angleRad = angleDegrees * PI / 180
    val cosVal = cos(angleRad)
    val sinVal = sin(angleRad)
    return Offset(
        (x * cosVal - y * sinVal).toFloat(),
        (x * sinVal + y * cosVal).toFloat()
    )
}

