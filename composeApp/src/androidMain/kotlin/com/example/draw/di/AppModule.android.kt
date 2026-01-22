package com.example.draw.di

import com.example.draw.data.repository.AndroidImageRepository
import com.example.draw.data.repository.ImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    // Inject AndroidContext vào Repository implementation
    single<ImageRepository> { AndroidImageRepository(androidContext()) }
}