package com.example.draw

import android.app.Application
import com.example.draw.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class DrawApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@DrawApplication) // <-- Đây là dòng sửa lỗi MissingAndroidContextException
        }
    }
}
