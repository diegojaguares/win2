package com.example.youtubeauto.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youtubeauto.model.YouTubeVideo

/**
 * Repositorio simple de videos.
 * Por ahora devuelve lista local + busqueda en memoria.
 * Listo para enchufar YouTube Data API v3 mas adelante.
 */
class YouTubeRepository {

    private val allVideos: List<YouTubeVideo> = YouTubeVideo.samples()

    private val _videos = MutableLiveData<List<YouTubeVideo>>(allVideos)
    val videos: LiveData<List<YouTubeVideo>> = _videos

    fun getAll(): List<YouTubeVideo> = allVideos

    fun getById(videoId: String): YouTubeVideo? =
        allVideos.find { it.id == videoId }

    fun search(query: String): List<YouTubeVideo> {
        if (query.isBlank()) return allVideos
        val q = query.trim().lowercase()
        val result = allVideos.filter {
            it.title.lowercase().contains(q) ||
            it.channelName.lowercase().contains(q) ||
            it.id.lowercase().contains(q)
        }
        _videos.postValue(result)
        return result
    }

    fun refresh() {
        _videos.postValue(allVideos)
    }
}

