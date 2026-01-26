package com.example.draw.di

import com.example.draw.data.service.FileStorageService
import com.example.draw.data.repository.ImageRepository
import com.example.draw.data.repository.IosImageRepository
import com.example.draw.platform.service.IosFileStorageService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<FileStorageService> { IosFileStorageService() }
    single<ImageRepository> { IosImageRepository() }
}