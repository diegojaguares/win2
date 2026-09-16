package com.example.youtubeauto

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubeauto.player.SurfaceRenderer
import com.example.youtubeauto.databinding.ActivityVideoProjectionBinding

/**
 * Actividad de proyección de video
 * Inspirado en Fermata Auto - permite reproducción de video en pantalla completa
 * 
 * NOTA: Esta actividad está diseñada para fines educativos y de demostración.
 * Android Auto bloqueará la reproducción de video mientras el vehículo esté en movimiento
 * por razones de seguridad. Funciona solo cuando el vehículo está detenido o en simulador.
 */
class VideoProjectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoProjectionBinding
    private var surfaceRenderer: SurfaceRenderer? = null
    private var currentVideoId: String = ""

    companion object {
        const val EXTRA_VIDEO_ID = "VIDEO_ID"
        const val EXTRA_VIDEO_TITLE = "VIDEO_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar pantalla completa y mantener pantalla encendida
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        
        binding = ActivityVideoProjectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        currentVideoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Video"
        
        // Mostrar título del video
        binding.videoTitle.text = videoTitle

        // Inicializar renderizador de superficie
        surfaceRenderer = SurfaceRenderer(binding.videoContainer, this)
        
        if (currentVideoId.isNotEmpty()) {
            surfaceRenderer?.initialize(currentVideoId)
        }
    }

    override fun onPause() {
        super.onPause()
        surfaceRenderer?.pause()
    }

    override fun onResume() {
        super.onResume()
        surfaceRenderer?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        surfaceRenderer?.release()
        surfaceRenderer = null
    }

    override fun onBackPressed() {
        surfaceRenderer?.stop()
        finish()
    }
}
