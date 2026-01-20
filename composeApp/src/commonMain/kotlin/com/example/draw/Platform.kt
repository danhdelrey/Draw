package com.example.draw

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform