package com.example.draw.di

import androidx.compose.ui.input.key.Key.Companion.G
import com.example.draw.data.datasource.local.DrawingRepository
import com.example.draw.data.datasource.local.DrawingRepositoryImpl
import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import com.example.draw.ui.feature.gallery.viewModel.GalleryScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        // 1. Chạy cấu hình riêng của từng platform (VD: androidContext)
        appDeclaration()

        // 2. Nạp các modules chung
        modules(
            platformModule,
            sharedModule
        )
    }
}

expect val platformModule: Module

val sharedModule = module {
    single<DrawingRepository> { DrawingRepositoryImpl(get()) }

    factory { GalleryScreenViewModel(get()) }
    factory { DrawingScreenViewModel(get()) }
}