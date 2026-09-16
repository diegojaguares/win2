package com.example.youtubeauto.player

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LifecycleOwner

/**
 * Reproductor YouTube basado en WebView (iframe embed).
 * Sin dependencias externas: funciona solo con el SDK.
 * Misma API publica que antes para no tocar VideoProjectionActivity.
 */
class SurfaceRenderer(
    private val parentView: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) {
    private var webView: WebView? = null
    private var isInitialized = false
    private var pendingVideoId: String? = null

    companion object {
        private const val TAG = "SurfaceRenderer"
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize(videoId: String) {
        if (isInitialized) {
            loadVideo(videoId)
            return
        }
        pendingVideoId = videoId

        val wv = WebView(parentView.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
        }
        parentView.addView(wv)
        webView = wv
        isInitialized = true
        pendingVideoId?.let {
            loadVideo(it)
            pendingVideoId = null
        }
    }

    fun loadVideo(videoId: String, startTime: Float = 0f) {
        val wv = webView ?: run { pendingVideoId = videoId; return }
        val start = startTime.toInt()
        val html = "<html><head><style>html,body{margin:0;padding:0;background:black;height:100%}</style></head>" +
            "<body><iframe width=\"100%\" height=\"100%\" " +
            "src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0&start=" + start + "\" " +
            "frameborder=\"0\" allow=\"autoplay; encrypted-media; fullscreen\" allowfullscreen></iframe></body></html>"
        try {
            wv.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
        } catch (e: Exception) {
            Log.e(TAG, "loadVideo fallo: ${e.message}")
        }
    }

    fun pause() {
        try { webView?.onPause() } catch (e: Exception) { Log.w(TAG, e.message ?: "pause fail") }
    }

    fun resume() {
        try { webView?.onResume() } catch (e: Exception) { Log.w(TAG, e.message ?: "resume fail") }
    }

    fun stop() {
        try { webView?.loadUrl("about:blank") } catch (e: Exception) { /* ignore */ }
    }

    fun release() {
        try {
            webView?.let {
                parentView.removeView(it)
                it.destroy()
            }
        } catch (e: Exception) {
            Log.w(TAG, e.message ?: "release fail")
        } finally {
            webView = null
            isInitialized = false
            pendingVideoId = null
        }
    }

    fun getView(): View? = webView
}
