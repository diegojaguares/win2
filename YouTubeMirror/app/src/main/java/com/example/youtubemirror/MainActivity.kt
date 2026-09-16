package com.example.youtubemirror

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.youtubemirror.databinding.ActivityMainBinding

/**
 * App espejo: abre directo en video, autoplay, fullscreen limpio.
 * Un toque muestra controles que se ocultan solos. Pensada para Screen2Auto.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var player: MirrorPlayer? = null
    private var index = 0
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { binding.overlay.visibility = View.GONE }

    companion object {
        val VIDEOS = listOf(
            Pair("jfKfPfyJRdk", "lofi hip hop radio"),
            Pair("5qap5aO4i9A", "lofi radio"),
            Pair("M7FIvfx5J10", "Music for Programming"),
            Pair("DWcJFNfaw9c", "Ambient Relaxation"),
            Pair("tGBRkQvf8B8", "Chill Music")
        )
        const val EXTRA_VIDEO_ID = "VIDEO_ID"
        private const val HIDE_DELAY = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startId = intent.getStringExtra(EXTRA_VIDEO_ID)
        val startIndex = VIDEOS.indexOfFirst { it.first == startId }.takeIf { it >= 0 } ?: 0
        index = startIndex

        player = MirrorPlayer(this, binding.videoContainer, binding.fullscreenHolder)
        playAt(index)

        binding.videoContainer.setOnClickListener { toggleOverlay() }
        binding.prevButton.setOnClickListener { playAt(index - 1); pokeOverlay() }
        binding.nextButton.setOnClickListener { playAt(index + 1); pokeOverlay() }
        binding.closeButton.setOnClickListener { finish() }
    }

    private fun playAt(i: Int) {
        index = (i + VIDEOS.size) % VIDEOS.size
        val video = VIDEOS[index]
        binding.videoTitle.text = video.second
        val firstTime = player == null
        if (firstTime) {
            player = MirrorPlayer(this, binding.videoContainer, binding.fullscreenHolder)
        }
        if (binding.videoContainer.childCount == 0) {
            player?.initialize(video.first)
        } else {
            player?.loadVideo(video.first)
        }
    }

    private fun toggleOverlay() {
        if (binding.overlay.visibility == View.VISIBLE) {
            hideHandler.removeCallbacks(hideRunnable)
            binding.overlay.visibility = View.GONE
        } else {
            pokeOverlay()
        }
    }

    private fun pokeOverlay() {
        binding.overlay.visibility = View.VISIBLE
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, HIDE_DELAY)
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.resume()
    }

    override fun onDestroy() {
        hideHandler.removeCallbacks(hideRunnable)
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (player?.onBackPressed() == true) return
        super.onBackPressed()
    }
}
