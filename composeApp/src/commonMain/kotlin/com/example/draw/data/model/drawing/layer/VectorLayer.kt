package com.example.draw.data.model.drawing.layer

import com.example.draw.data.model.drawing.base.DrawingPath

data class VectorLayer(
    override val id: String,
    override val name: String,
    override val isVisible: Boolean = true,
    override val isLocked: Boolean = false,
    override val opacity: Float = 1f,
    val paths: List<DrawingPath> = emptyList()
) : Layer()
