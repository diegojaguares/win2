package com.example.youtubeauto.media

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
 * Medios para Android Auto + espejo de caratulas en vivo en la pantalla del carro.
 */
class YouTubeMediaService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat
    private val artHandler = Handler(Looper.getMainLooper())
    private var artIndex = 0
    private var cycling = false

    companion object {
        const val ROOT_ID = "root"
        private const val TAG = "YouTubeMediaSvc"
    }

    override fun onCreate() {
        super.onCreate()
        session = MediaSessionCompat(this, TAG)
        sessionToken = session.sessionToken
        session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        session.setCallback(SessionCallback())
        session.isActive = true
        Log.d(TAG, "service created")
    }

    private val artRunnable = object : Runnable {
        override fun run() {
            if (!cycling) return
            val videos = YouTubeVideo.samples()
            val v = videos[artIndex % videos.size]
            artIndex++
            val art = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, v.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, v.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, v.channelName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, v.thumbnailUrl)
                .build()
            session.setMetadata(art)
            artHandler.postDelayed(this, 4000)
        }
    }

    private fun startArtCycle() {
        if (cycling) return
        cycling = true
        artHandler.post(artRunnable)
    }

    private fun stopArtCycle() {
        cycling = false
        artHandler.removeCallbacks(artRunnable)
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
            if (video != null) { playVideo(video) } else { playVideo(YouTubeVideo.samples().first()) }
        }

        override fun onPlay() {
            playVideo(YouTubeVideo.samples().first())
        }

        override fun onPause() {
            stopArtCycle()
            session.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
                    .build()
            )
        }

        override fun onStop() {
            stopArtCycle()
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
                    .setActions(PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_STOP)
                    .build()
            )
            val intent = Intent(this@YouTubeMediaService, VideoProjectionActivity::class.java)
            intent.putExtra(VideoProjectionActivity.EXTRA_VIDEO_ID, video.id)
            intent.putExtra(VideoProjectionActivity.EXTRA_VIDEO_TITLE, video.title)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            startArtCycle()
            Log.d(TAG, "play video")
        }
    }

    override fun onDestroy() {
        stopArtCycle()
        session.isActive = false
        session.release()
        super.onDestroy()
    }
}
