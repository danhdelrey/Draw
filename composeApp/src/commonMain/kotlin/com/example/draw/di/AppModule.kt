package com.example.draw.di

import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    factory { DrawingScreenViewModel() }
}

fun initKoin(
    extraModules: List<Module> = emptyList()
) {
    startKoin {
        modules(appModule + extraModules)
    }
}
