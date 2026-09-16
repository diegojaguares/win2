package com.example.youtubeauto.player

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosironi.youtubeplayer.YouTubePlayer
import com.pierfrancescosironi.youtubeplayer.YouTubePlayerView
import com.pierfrancescosironi.youtubeplayer.listener.AbstractYouTubeListener

/**
 * Renderizador de superficie para video de YouTube
 * Inspirado en Fermata Auto
 */
class SurfaceRenderer(
    private val parentView: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) {
    private var youTubePlayerView: YouTubePlayerView? = null
    private var youTubePlayer: YouTubePlayer? = null
    private var isInitialized = false
    private var pendingVideoId: String? = null

    companion object {
        private const val TAG = "SurfaceRenderer"
    }

    fun initialize(videoId: String) {
        if (isInitialized) {
            loadVideo(videoId)
            return
        }
        pendingVideoId = videoId

        youTubePlayerView = YouTubePlayerView(parentView.context).apply {
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            parentView.addView(this, params)
        }

        try {
            lifecycleOwner.lifecycle.addObserver(youTubePlayerView!!)
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo registrar lifecycle observer: ${e.message}")
        }

        youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubeListener() {
            override fun onReady(player: YouTubePlayer) {
                youTubePlayer = player
                isInitialized = true
                pendingVideoId?.let {
                    player.loadVideo(it, 0f)
                    pendingVideoId = null
                }
            }

            override fun onError(player: YouTubePlayer, error: com.pierfrancescosironi.youtubeplayer.PlayerError) {
                Log.e(TAG, "YouTube error: $error")
            }
        })
    }

    fun loadVideo(videoId: String, startTime: Float = 0f) {
        if (isInitialized) {
            youTubePlayer?.loadVideo(videoId, startTime)
        } else {
            pendingVideoId = videoId
        }
    }

    fun pause() {
        try { youTubePlayer?.pause() } catch (e: Exception) { Log.w(TAG, e.message ?: "pause fail") }
    }

    fun resume() {
        try { youTubePlayer?.play() } catch (e: Exception) { Log.w(TAG, e.message ?: "resume fail") }
    }

    fun stop() {
        try { youTubePlayer?.pause() } catch (e: Exception) { /* ignore */ }
    }

    fun release() {
        try {
            youTubePlayer?.pause()
            youTubePlayerView?.removeAllListeners()
            youTubePlayerView?.let { parentView.removeView(it) }
        } catch (e: Exception) {
            Log.w(TAG, e.message ?: "release fail")
        } finally {
            youTubePlayerView = null
            youTubePlayer = null
            isInitialized = false
            pendingVideoId = null
        }
    }

    fun getView(): View? = youTubePlayerView
}

