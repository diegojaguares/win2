package com.example.youtubeauto

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.youtubeauto.screens.YouTubeHomeScreen

/**
 * Servicio principal de Android Auto
 * Inspirado en la arquitectura de Fermata Auto
 */
class YouTubeCarAppService : CarAppService() {

    override fun onCreateSession(): Session {
        return YouTubeSession()
    }

    inner class YouTubeSession : Session() {
        override fun onCreateScreen(intent: Intent): Screen {
            return YouTubeHomeScreen(carContext)
        }
    }
}

