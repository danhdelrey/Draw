package com.example.draw.di

import com.example.draw.ui.feature.drawing.viewModel.DrawingScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


// Thêm tham số appDeclaration có giá trị mặc định là rỗng
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
    factory { DrawingScreenViewModel(get()) }
}