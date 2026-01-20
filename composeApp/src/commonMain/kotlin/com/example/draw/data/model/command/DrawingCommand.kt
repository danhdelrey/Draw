package com.example.draw.data.model.command

import com.example.draw.data.model.base.DrawingPath

sealed class DrawingCommand {
    data class AddPath(val layerId: String, val path: DrawingPath) : DrawingCommand()
    data class ToggleLayerVisibility(val layerId: String) : DrawingCommand()
    data class DeleteLayer(val layerId: String) : DrawingCommand()
}
