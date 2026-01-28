package com.example.draw.data.model.canvas

import kotlinx.serialization.Serializable

/**
 * Canvas configuration settings.
 *
 * Design principles:
 * - Immutable configuration object
 * - Centralized defaults
 * - Easy to extend with new settings
 * - Support for different canvas sizes and presets
 */
@Serializable
data class CanvasConfig(
    val width: Float = DEFAULT_WIDTH,
    val height: Float = DEFAULT_HEIGHT,
    val backgroundColor: Long = DEFAULT_BACKGROUND_COLOR,
    val maxLayers: Int = DEFAULT_MAX_LAYERS,
    val maxUndoSteps: Int = DEFAULT_MAX_UNDO_STEPS,
    val enablePressure: Boolean = true,
    val enableSmoothing: Boolean = true
) {
    companion object {
        // Default canvas dimensions
        const val DEFAULT_WIDTH = 1080f
        const val DEFAULT_HEIGHT = 1080f

        // Default settings
        const val DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF  // White
        const val DEFAULT_MAX_LAYERS = 50
        const val DEFAULT_MAX_UNDO_STEPS = 100

        /**
         * Predefined canvas size presets
         */
        object Presets {
            val SQUARE_1080 = CanvasConfig(1080f, 1080f)
            val SQUARE_2048 = CanvasConfig(2048f, 2048f)
            val SQUARE_4096 = CanvasConfig(4096f, 4096f)

            val HD_LANDSCAPE = CanvasConfig(1920f, 1080f)
            val HD_PORTRAIT = CanvasConfig(1080f, 1920f)

            val FOUR_K_LANDSCAPE = CanvasConfig(3840f, 2160f)
            val FOUR_K_PORTRAIT = CanvasConfig(2160f, 3840f)

            val A4_PORTRAIT_72DPI = CanvasConfig(595f, 842f)  // 210x297mm at 72 DPI
            val A4_LANDSCAPE_72DPI = CanvasConfig(842f, 595f)

            val A4_PORTRAIT_300DPI = CanvasConfig(2480f, 3508f)  // 210x297mm at 300 DPI
            val A4_LANDSCAPE_300DPI = CanvasConfig(3508f, 2480f)
        }

        /**
         * Create a custom canvas config with specific aspect ratio
         */
        fun withAspectRatio(width: Float, aspectRatio: Float): CanvasConfig =
            CanvasConfig(width = width, height = width * aspectRatio)

        /**
         * Create a square canvas config
         */
        fun square(size: Float): CanvasConfig =
            CanvasConfig(width = size, height = size)
    }
}
