package com.example.draw.data.model.util

/**
 * WasmJS implementation of currentTimeMillis
 * Uses JavaScript Date.now() via JsFun annotation
 */
@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => Date.now()")
private external fun dateNow(): Double

actual fun currentTimeMillis(): Long = dateNow().toLong()

