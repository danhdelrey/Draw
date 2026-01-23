package com.example.draw.data.model.util

import kotlin.random.Random

/**
 * Utility functions for model operations that need platform independence.
 */

/**
 * Generate a unique ID for models.
 * This is a simple implementation - in production, you might want to use UUID or a more sophisticated ID generator.
 */
fun generateId(): String {
    val timestamp = currentTimeMillis()
    val random = Random.nextInt(0, 999999)
    return "${timestamp}-${random}"
}

/**
 * Get current time in milliseconds.
 * Platform-independent wrapper for timestamp generation.
 */
expect fun currentTimeMillis(): Long

