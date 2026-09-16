package com.example.youtubeauto.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.LruCache
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Descarga caratulas de YouTube y las cachea. Sin dependencias.
 */
object ThumbnailLoader {

    private const val TAG = "ThumbLoader"
    private val cache = LruCache<String, Bitmap>(24)
    private val executor = Executors.newFixedThreadPool(2)
    private val main = Handler(Looper.getMainLooper())

    fun load(url: String, maxWidth: Int, cb: (Bitmap?) -> Unit) {
        val hit = cache.get(url)
        if (hit != null) {
            cb(hit)
            return
        }
        executor.execute {
            var bmp: Bitmap? = null
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.instanceFollowRedirects = true
                val bytes = conn.inputStream.use { it.readBytes() }
                val bounds = BitmapFactory.Options()
                bounds.inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                val sample = sampleSize(bounds.outWidth, maxWidth)
                val opts = BitmapFactory.Options()
                opts.inSampleSize = sample
                val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
                if (decoded != null) {
                    bmp = scaleToWidth(decoded, maxWidth)
                    if (bmp !== decoded) { decoded.recycle() }
                    cache.put(url, bmp as Bitmap)
                }
            } catch (e: Exception) {
                Log.w(TAG, "fallo descarga")
            }
            val out = bmp
            main.post { cb(out) }
        }
    }

    private fun sampleSize(width: Int, maxWidth: Int): Int {
        if (width <= 0) return 1
        var s = 1
        var w = width
        while (w / 2 >= maxWidth) { w /= 2; s *= 2 }
        return s
    }

    private fun scaleToWidth(src: Bitmap, maxWidth: Int): Bitmap {
        if (src.width <= maxWidth) return src
        val h = (src.height * maxWidth) / src.width
        return Bitmap.createScaledBitmap(src, maxWidth, h, true)
    }
}
