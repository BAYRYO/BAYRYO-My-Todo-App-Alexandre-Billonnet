package com.example.mytodoapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
