package com.example.draw.di

import com.example.draw.data.repository.ImageRepository
import com.example.draw.data.repository.WebImageRepository
import org.koin.dsl.module

actual val platformModule = module {
    single<ImageRepository> { WebImageRepository() }
}