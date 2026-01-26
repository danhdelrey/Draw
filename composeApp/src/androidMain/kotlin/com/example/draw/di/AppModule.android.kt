package com.example.draw.di

import com.example.draw.data.service.FileStorageService
import com.example.draw.data.repository.AndroidImageRepository
import com.example.draw.data.repository.ImageRepository
import com.example.draw.platform.service.AndroidFileStorageService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<FileStorageService> { AndroidFileStorageService(get()) }
    single<ImageRepository> { AndroidImageRepository(androidContext()) }
}