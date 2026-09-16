package com.example.youtubeauto.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.youtubeauto.R
import com.example.youtubeauto.model.YouTubeVideo
import com.example.youtubeauto.repository.YouTubeRepository
import com.example.youtubeauto.util.ThumbnailLoader

/**
 * Parrilla de videos con caratulas reales. Espejo visual de YouTube en el carro.
 */
class YouTubeHomeScreen(carContext: CarContext) : Screen(carContext) {

    private val repository = YouTubeRepository()
    private val thumbs = mutableMapOf<String, Bitmap>()
    private val pending = mutableSetOf<String>()

    private fun iconFor(video: YouTubeVideo): CarIcon {
        val bmp = thumbs[video.id]
        if (bmp != null) {
            try {
                return CarIcon.Builder(IconCompat.createWithBitmap(bmp)).build()
            } catch (e: Exception) {
                Log.w("YouTubeCarSvc", "icono malo")
            }
        }
        if (pending.add(video.id)) {
            ThumbnailLoader.load(video.thumbnailUrl, 240) { loaded ->
                pending.remove(video.id)
                if (loaded != null) {
                    thumbs[video.id] = loaded
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

    override fun onGetTemplate(): Template {
        Log.d("YouTubeCarSvc", "Home onGetTemplate")
        val listBuilder = ItemList.Builder()
        repository.getAll().forEach { video ->
            val item = GridItem.Builder()
                .setTitle(video.title)
                .setText(video.channelName)
                .setImage(iconFor(video))
                .setOnClickListener {
                    screenManager.push(YouTubeVideoDetailScreen(carContext, video))
                }
                .build()
            listBuilder.addItem(item)
        }
        return GridTemplate.Builder()
            .setTitle(carContext.getString(R.string.youtube_title))
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .build()
    }
}
