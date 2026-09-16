package com.example.youtubeauto.model

import androidx.car.app.annotations.NonNull
import com.pierfrancescosironi.youtubeplayer.YouTubePlayerListener

/**
 * Modelo de datos para un video de YouTube
 */
data class YouTubeVideo(
    val id: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val duration: String = ""
) {
    companion object {
        fun fromVideoId(videoId: String, title: String = "Video sin título"): YouTubeVideo {
            return YouTubeVideo(
                id = videoId,
                title = title,
                channelName = "YouTube",
                thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
            )
        }
    }
}

/**
 * Listener simplificado para el reproductor de YouTube
 */
interface SimpleYouTubeListener : YouTubePlayerListener {
    override fun onReady(youTubePlayer: com.pierfrancescosironi.youtubeplayer.YouTubePlayer) {
        // Implementación por defecto vacía
    }

    override fun onStateChange(
        youTubePlayer: com.pierfrancescosironi.youtubeplayer.YouTubePlayer,
        state: com.pierfrancescosironi.youtubeplayer.PlayerState
    ) {
        // Implementación por defecto vacía
    }
}
