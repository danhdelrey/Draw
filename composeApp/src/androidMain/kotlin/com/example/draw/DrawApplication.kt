package com.example.draw

import android.app.Application
import com.example.draw.di.initKoin

class DrawApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
