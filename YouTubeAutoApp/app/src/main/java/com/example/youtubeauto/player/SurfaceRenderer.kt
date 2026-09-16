package com.example.youtubeauto.player

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosironi.youtubeplayer.YouTubePlayer
import com.pierfrancescosironi.youtubeplayer.YouTubePlayerView
import com.pierfrancescosironi.youtubeplayer.listener.AbstractYouTubeListener

/**
 * Renderizador de superficie para video de YouTube
 * Inspirado en la implementación de Fermata Auto
 */
class SurfaceRenderer(
    private val parentView: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) {
    private var youTubePlayerView: YouTubePlayerView? = null
    private var youTubePlayer: YouTubePlayer? = null
    private var isInitialized = false

    /**
     * Inicializa el reproductor de YouTube
     */
    fun initialize(videoId: String) {
        if (isInitialized) return

        youTubePlayerView = YouTubePlayerView(parentView.context)
        
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        
        parentView.addView(youTubePlayerView, params)

        youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubeListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@SurfaceRenderer.youTubePlayer = youTubePlayer
                isInitialized = true
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: com.pierfrancescosironi.youtubeplayer.PlayerError) {
                // Manejo de errores
            }
        })
    }

    /**
     * Carga un video específico
     */
    fun loadVideo(videoId: String, startTime: Float = 0f) {
        youTubePlayer?.loadVideo(videoId, startTime)
    }

    /**
     * Pausa la reproducción
     */
    fun pause() {
        youTubePlayer?.pause()
    }

    /**
     * Reanuda la reproducción
     */
    fun resume() {
        youTubePlayer?.play()
    }

    /**
     * Detiene la reproducción
     */
    fun stop() {
        youTubePlayer?.stopVideo()
    }

    /**
     * Libera los recursos del reproductor
     */
    fun release() {
        youTubePlayerView?.removeAllListeners()
        youTubePlayerView?.let { parentView.removeView(it) }
        youTubePlayerView = null
        youTubePlayer = null
        isInitialized = false
    }

    /**
     * Obtiene la vista del reproductor
     */
    fun getView(): View? = youTubePlayerView
}
