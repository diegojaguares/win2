package com.example.youtubeauto.screens

import android.content.Intent
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

/**
 * Detalle del video: muestra info + accion Reproducir.
 * El video se abre en el telefono (VideoProjectionActivity),
 * patron tipo Fermata: en el head-unit solo van templates.
 */
class YouTubeVideoDetailScreen(
    carContext: CarContext,
    private val video: YouTubeVideo
) : Screen(carContext) {

    private fun appIcon(): CarIcon {
        return try {
            CarIcon.Builder(
                IconCompat.createWithResource(carContext, R.drawable.ic_youtube_logo)
            ).build()
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

    override fun getTemplate(): Template {
        val row = Row.Builder()
            .setTitle(video.title)
            .addText(video.channelName)
            .addText(video.id)
            .setImage(appIcon())
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

