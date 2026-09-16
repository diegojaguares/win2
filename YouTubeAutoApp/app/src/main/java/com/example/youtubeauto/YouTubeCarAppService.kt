package com.example.youtubeauto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.car.app.CarAppService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.youtubeauto.screens.YouTubeHomeScreen
import kotlinx.coroutines.launch

/**
 * Servicio principal de Android Auto
 * Inspirado en la arquitectura de Fermata Auto
 */
class YouTubeCarAppService : CarAppService() {

    override fun createCarAppSession(): Session {
        return YouTubeSession()
    }

    inner class YouTubeSession : Session() {
        override fun onCreateScreen(intent: Intent): Screen {
            return YouTubeHomeScreen(carContext)
        }
    }
}
