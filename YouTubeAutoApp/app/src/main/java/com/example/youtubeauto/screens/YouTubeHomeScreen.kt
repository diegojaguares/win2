package com.example.youtubeauto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.youtubeauto.R
import com.example.youtubeauto.repository.YouTubeRepository

/**
 * Pantalla principal de YouTube para Android Auto
 * Muestra grid de videos sugeridos. Click -> Detail.
 */
class YouTubeHomeScreen(carContext: CarContext) : Screen(carContext) {

    private val repository = YouTubeRepository()

    private fun appIcon(): CarIcon {
        return try {
            CarIcon.Builder(
                IconCompat.createWithResource(carContext, R.drawable.ic_youtube_logo)
            ).build()
        } catch (e: Exception) {
            CarIcon.APP_ICON
        }
    }

    override fun getTemplate(): Template {
        val itemListBuilder = ItemList.Builder()

        repository.getAll().forEach { video ->
            val gridItem = GridItem.Builder()
                .setTitle(video.title)
                .setText(video.channelName)
                .setImage(appIcon())
                .setOnClickListener {
                    screenManager.push(YouTubeVideoDetailScreen(carContext, video))
                }
                .build()
            itemListBuilder.addItem(gridItem)
        }

        return GridTemplate.Builder()
            .setTitle(carContext.getString(R.string.youtube_title))
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(itemListBuilder.build())
            .build()
    }
}

