package com.example.draw.data.model.layer

sealed class Layer {

    abstract val id: String
    abstract val name: String
    abstract val isVisible: Boolean
    abstract val isLocked: Boolean
    abstract val opacity: Float        // 0f..1f

}

