package com.example.youtubeauto

import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.example.youtubeauto.screens.YouTubeHomeScreen

/**
 * Servicio principal de Android Auto.
 * ALLOW_ALL_HOSTS solo para desarrollo: en produccion restringir al host real.
 */
class YouTubeCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        Log.d("YouTubeCarSvc", "onCreateSession")
        return YouTubeSession()
    }

    inner class YouTubeSession : Session() {
        override fun onCreateScreen(intent: Intent): Screen {
            Log.d("YouTubeCarSvc", "onCreateScreen")
            return YouTubeHomeScreen(carContext)
        }
    }
}
