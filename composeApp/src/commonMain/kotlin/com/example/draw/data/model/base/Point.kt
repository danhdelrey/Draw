package com.example.draw.data.model.base

import androidx.compose.ui.geometry.Offset

data class Point(
    val x: Float,
    val y: Float
){
    fun Point.toOffset(): Offset =
        Offset(x, y)

}

