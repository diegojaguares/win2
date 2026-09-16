package com.example.youtubesdl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Display
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.smartdevicelink.streaming.video.SdlRemoteDisplay

/**
 * Pantalla remota: se dibuja en el head unit via streaming de video SDL.
 */
class CarDisplay(context: Context, display: Display) : SdlRemoteDisplay(context, display) {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(context)
        root.setBackgroundColor(Color.BLACK)
        val wv = buildWebView()
        root.addView(
            wv,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        setContentView(root)
        webView = wv
        loadVideo(MainActivity.DEFAULT_VIDEO)
    }

    override fun onViewResized(width: Int, height: Int) {
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildWebView(): WebView {
        val wv = WebView(context)
        wv.settings.javaScriptEnabled = true
        wv.settings.mediaPlaybackRequiresUserGesture = false
        wv.settings.domStorageEnabled = true
        wv.webChromeClient = WebChromeClient()
        wv.webViewClient = WebViewClient()
        wv.setBackgroundColor(Color.BLACK)
        return wv
    }

    fun loadVideo(videoId: String) {
        val wv = webView ?: return
        val a = "<html><head><style>html,body{margin:0;padding:0;background:black;height:100%}</style></head><body>"
        val b = "<iframe width=100% height=100% src=https://www.youtube.com/embed/"
        val c = "?autoplay=1&rel=0 frameborder=0 allowfullscreen></iframe></body></html>"
        wv.loadDataWithBaseURL("https://www.youtube.com", a + b + videoId + c, "text/html", "utf-8", null)
    }
}
