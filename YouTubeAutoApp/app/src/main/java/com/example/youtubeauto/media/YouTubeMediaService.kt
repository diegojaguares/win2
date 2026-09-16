package com.example.youtubeauto.media

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.youtubeauto.VideoProjectionActivity
import com.example.youtubeauto.model.YouTubeVideo

/**
 * Servicio de medios para Android Auto (fuentes de Medios).
 * Al dar play abre el video en el telefono y publica estado al carro.
 */
class YouTubeMediaService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat

    companion object {
        const val ROOT_ID = "root"
        private const val TAG = "YouTubeMediaSvc"
    }

    override fun onCreate() {
        super.onCreate()
        session = MediaSessionCompat(this, TAG)
        sessionToken = session.sessionToken
        session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        session.setCallback(SessionCallback())
        session.isActive = true
        Log.d(TAG, "service created")
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        val items = YouTubeVideo.samples().map { video ->
            val desc = MediaDescriptionCompat.Builder()
                .setMediaId(video.id)
                .setTitle(video.title)
                .setSubtitle(video.channelName)
                .setIconUri(Uri.parse(video.thumbnailUrl))
                .build()
            MediaItem(desc, MediaItem.FLAG_PLAYABLE)
        }.toMutableList()
        result.sendResult(items)
    }

    private inner class SessionCallback : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val video = YouTubeVideo.samples().firstOrNull { it.id == mediaId }
                ?: YouTubeVideo.samples().first()
            playVideo(video)
        }

        override fun onPlay() {
            playVideo(YouTubeVideo.samples().first())
        }

        override fun onPause() {
            session.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
                    .build()
            )
        }

        override fun onStop() {
            session.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY)
                    .build()
            )
        }

        private fun playVideo(video: YouTubeVideo) {
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, video.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, video.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, video.channelName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, video.thumbnailUrl)
                .build()
            session.setMetadata(metadata)
            session.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .setActions(
                        PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_STOP or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                    .build()
            )
            val intent = Intent(this@YouTubeMediaService, VideoProjectionActivity::class.java).apply {
                putExtra(VideoProjectionActivity.EXTRA_VIDEO_ID, video.id)
                putExtra(VideoProjectionActivity.EXTRA_VIDEO_TITLE, video.title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            Log.d(TAG, "play: " + video.id)
        }
    }

    override fun onDestroy() {
        session.isActive = false
        session.release()
        super.onDestroy()
    }
}
