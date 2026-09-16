package com.example.youtubemirror

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

class MirrorPlayer(
    private val activity: Activity,
    private val container: ViewGroup,
    private val fullscreenHolder: FrameLayout,
) {

    private var webView: WebView? = null
    private var customView: View? = null
    private var customCallback: WebChromeClient.CustomViewCallback? = null

    companion object {
        private const val TAG = "MirrorPlayer"
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize(videoId: String) {
        if (webView != null) {
            loadVideo(videoId)
            return
        }
        val wv = WebView(container.context)
        wv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        wv.settings.javaScriptEnabled = true
        wv.settings.mediaPlaybackRequiresUserGesture = false
        wv.settings.domStorageEnabled = true
        wv.webChromeClient = MirrorChromeClient()
        wv.webViewClient = WebViewClient()
        container.addView(wv)
        webView = wv
        loadVideo(videoId)
    }

    fun loadVideo(videoId: String) {
        val wv = webView ?: return
        val htmlA = "<html><head><style>html,body{margin:0;padding:0;background:black;height:100%}</style></head><body>"
        val htmlB = "<iframe width=100% height=100% src=https://www.youtube.com/embed/"
        val htmlC = "?autoplay=1&rel=0 frameborder=0 allowfullscreen></iframe></body></html>"
        try {
            wv.loadDataWithBaseURL("https://www.youtube.com", htmlA + htmlB + videoId + htmlC, "text/html", "utf-8", null)
        } catch (e: Exception) {
            Log.e(TAG, "loadVideo fallo")
        }
    }

    fun pause() {
        try { webView?.onPause() } catch (e: Exception) { }
    }

    fun resume() {
        try { webView?.onResume() } catch (e: Exception) { }
    }

    fun release() {
        try {
            hideCustomView()
            webView?.let { v ->
                container.removeView(v)
                v.destroy()
            }
        } catch (e: Exception) {
        } finally {
            webView = null
        }
    }

    fun onBackPressed(): Boolean {
        if (customView != null) {
            hideCustomView()
            return true
        }
        return false
    }

    private fun hideCustomView() {
        val cv = customView
        if (cv != null) { fullscreenHolder.removeView(cv) }
        customView = null
        try { customCallback?.onCustomViewHidden() } catch (e: Exception) { }
        customCallback = null
        fullscreenHolder.visibility = View.GONE
    }

    private inner class MirrorChromeClient : WebChromeClient() {
        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (customView != null) {
                try { callback.onCustomViewHidden() } catch (e: Exception) { }
                return
            }
            customView = view
            customCallback = callback
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            fullscreenHolder.addView(view, lp)
            fullscreenHolder.visibility = View.VISIBLE
        }

        override fun onHideCustomView() {
            hideCustomView()
        }
    }
}
