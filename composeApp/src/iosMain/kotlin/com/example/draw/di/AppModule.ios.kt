package com.example.draw.di

import com.example.draw.data.repository.ImageRepository
import com.example.draw.data.repository.IosImageRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<ImageRepository> { IosImageRepository() }
}