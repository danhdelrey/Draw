package com.example.draw

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.draw.di.initKoin
import kotlinx.browser.document
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {
        logger(PrintLogger(Level.DEBUG))
    }
    ComposeViewport(document.body!!) {
        App()
    }
}