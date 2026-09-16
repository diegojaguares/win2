package com.example.youtubeauto.screens

import android.content.Intent
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.youtubeauto.R
import com.example.youtubeauto.VideoProjectionActivity
import com.example.youtubeauto.model.YouTubeVideo
import com.example.youtubeauto.util.ThumbnailLoader

/**
 * Detalle con caratula. Reproducir abre el video en el telefono.
 */
class YouTubeVideoDetailScreen(
    carContext: CarContext,
    private val video: YouTubeVideo
) : Screen(carContext) {

    private var thumb: CarIcon? = null
    private var requested = false

    private fun icon(): CarIcon {
        thumb?.let { return it }
        if (!requested) {
            requested = true
            ThumbnailLoader.load(video.thumbnailUrl, 320) { loaded ->
                if (loaded != null) {
                    try {
                        thumb = CarIcon.Builder(IconCompat.createWithBitmap(loaded)).build()
                    } catch (e: Exception) {
                        Log.w("YouTubeCarSvc", "icono malo")
                    }
                    invalidate()
                }
            }
        }
        return try {
            CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_youtube_logo)).build()
        } catch (e: Exception) {
            CarIcon.APP_ICON
        }
    }

    private fun launchVideo() {
        val intent = Intent(carContext, VideoProjectionActivity::class.java).apply {
            putExtra(VideoProjectionActivity.EXTRA_VIDEO_ID, video.id)
            putExtra(VideoProjectionActivity.EXTRA_VIDEO_TITLE, video.title)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        carContext.startActivity(intent)
    }

    override fun onGetTemplate(): Template {
        Log.d("YouTubeCarSvc", "Detail onGetTemplate")
        val row = Row.Builder()
            .setTitle(video.title)
            .addText(video.channelName)
            .addText(video.id)
            .setImage(icon())
            .setOnClickListener { launchVideo() }
            .build()
        val playAction = Action.Builder()
            .setTitle(carContext.getString(R.string.play_video))
            .setOnClickListener { launchVideo() }
            .build()
        val pane = Pane.Builder()
            .addRow(row)
            .addAction(playAction)
            .build()
        return PaneTemplate.Builder(pane)
            .setTitle(video.title)
            .setHeaderAction(Action.BACK)
            .build()
    }
}
