package com.example.draw.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {

}

fun initKoin(
    extraModules: List<Module> = emptyList()
) {
    startKoin {
        modules(appModule + extraModules)
    }
}
