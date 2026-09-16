package com.example.youtubeauto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubeauto.screens.YouTubeHomeScreen

/**
 * Actividad principal para modo standalone (fuera de Android Auto)
 * Permite probar la aplicación directamente en el teléfono
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // En modo standalone, mostramos un mensaje explicativo
        // La funcionalidad completa está disponible solo en Android Auto
        setContentView(R.layout.activity_main)
    }
}
