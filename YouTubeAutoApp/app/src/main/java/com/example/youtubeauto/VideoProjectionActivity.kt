package com.example.youtubeauto

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubeauto.databinding.ActivityVideoProjectionBinding
import com.example.youtubeauto.player.SurfaceRenderer

/**
 * Actividad de proyeccion de video
 * Inspirado en Fermata Auto - permite reproduccion en pantalla completa
 *
 * NOTA: fines educativos. Android Auto bloquea video en movimiento.
 * Funciona detenido o en simulador. En Auto real el video se ve en el telefono.
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

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityVideoProjectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentVideoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Video"

        binding.videoTitle.text = videoTitle

        if (currentVideoId.isBlank()) {
            Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        surfaceRenderer = SurfaceRenderer(binding.videoContainer, this)
        surfaceRenderer?.initialize(currentVideoId)

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                surfaceRenderer?.stop()
                finish()
            }
        })
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
}

