package com.example.youtubeauto.screens

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.example.youtubeauto.R
import com.example.youtubeauto.VideoProjectionActivity
import com.example.youtubeauto.model.YouTubeVideo

/**
 * Pantalla de detalles del video para Android Auto
 * Muestra información del video seleccionado y opciones de reproducción
 */
class YouTubeVideoDetailScreen(
    carContext: CarContext,
    private val video: YouTubeVideo
) : Screen(carContext) {

    companion object {
        private const val TAG = "YouTubeVideoDetail"
    }

    override fun getTemplate(): Template {
        return PaneTemplate.Builder().apply {
            setTitle(video.title)
            setHeaderAction(Action.BACK)
            
            // Crear imagen del thumbnail
            val imageMetadata = ImageMetadata.Builder()
                .setUri(video.thumbnailUrl)
                .build()
            
            // Agregar acciones de reproducción
            val playAction = Action.Builder()
                .setTitle(carContext.getString(R.string.play_video))
                .setOnClickListener {
                    // Lanzar actividad de proyección de video
                    val intent = carContext.intentBuilder(VideoProjectionActivity::class.java)
                        .putExtra("VIDEO_ID", video.id)
                        .putExtra("VIDEO_TITLE", video.title)
                        .build()
                    carContext.startActivity(intent)
                }
                .build()
            
            setRootAction(playAction)
            
        }.build()
    }
}
