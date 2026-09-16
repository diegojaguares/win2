package com.example.youtubeauto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubeauto.model.YouTubeVideo

/**
 * Actividad principal para modo standalone (fuera de Android Auto).
 * Permite probar sin DHU.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPlaySample)?.setOnClickListener {
            val sample = YouTubeVideo.samples().first()
            val intent = Intent(this, VideoProjectionActivity::class.java).apply {
                putExtra(VideoProjectionActivity.EXTRA_VIDEO_ID, sample.id)
                putExtra(VideoProjectionActivity.EXTRA_VIDEO_TITLE, sample.title)
            }
            startActivity(intent)
        }
    }
}

