package com.example.youtubeauto.model

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
        fun fromVideoId(videoId: String, title: String = "Video sin titulo"): YouTubeVideo {
            return YouTubeVideo(
                id = videoId,
                title = title,
                channelName = "YouTube",
                thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
            )
        }

        fun samples(): List<YouTubeVideo> = listOf(
            fromVideoId("jfKfPfyJRdk", "lofi hip hop radio"),
            fromVideoId("5qap5aO4i9A", "lofi radio"),
            fromVideoId("M7FIvfx5J10", "Music for Programming"),
            fromVideoId("DWcJFNfaw9c", "Ambient Relaxation"),
            fromVideoId("tGBRkQvf8B8", "Chill Music")
        )
    }
}

