package com.example.youtubeauto.screens

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.example.youtubeauto.R
import com.example.youtubeauto.VideoProjectionActivity
import com.example.youtubeauto.model.YouTubeVideo

/**
 * Pantalla principal de YouTube para Android Auto
 * Muestra una lista de videos sugeridos y permite seleccionar uno para reproducir
 */
class YouTubeHomeScreen(carContext: CarContext) : Screen(carContext) {

    companion object {
        private const val TAG = "YouTubeHomeScreen"
        
        // Lista de videos de ejemplo para demostración
        val SAMPLE_VIDEOS = listOf(
            YouTubeVideo.fromVideoId("jfKfPfyJRdk", "lofi hip hop radio"),
            YouTubeVideo.fromVideoId("5qap5aO4i9A", "lofi radio"),
            YouTubeVideo.fromVideoId("M7FIvfx5J10", "Music for Programming"),
            YouTubeVideo.fromVideoId("DWcJFNfaw9c", "Ambient Relaxation"),
            YouTubeVideo.fromVideoId("tGBRkQvf8B8", "Chill Music")
        )
    }

    override fun getTemplate(): Template {
        return GridTemplate.Builder().apply {
            setTitle(carContext.getString(R.string.youtube_title))
            setHeaderAction(Action.APP_ICON)
            
            // Agregar items de video en formato grid
            val itemList = ItemList.Builder()
            
            SAMPLE_VIDEOS.forEach { video ->
                val gridItem = GridItem.Builder().apply {
                    setImage(
                        ImageMetadata.Builder()
                            .setUri(video.thumbnailUrl)
                            .build()
                    )
                    setTitle(video.title)
                    setText(video.channelName)
                    setOnClickListener {
                        // Al hacer clic, lanzar la actividad de reproducción de video
                        val intent = carContext.intentBuilder(VideoProjectionActivity::class.java)
                            .putExtra("VIDEO_ID", video.id)
                            .putExtra("VIDEO_TITLE", video.title)
                            .build()
                        carContext.startActivity(intent)
                    }
                    build()
                }
                itemList.addItem(gridItem.build())
            }
            
            setSingleList(itemList.build())
        }.build()
    }
}
