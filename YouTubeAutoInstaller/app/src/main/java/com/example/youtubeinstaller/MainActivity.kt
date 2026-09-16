package com.example.youtubeinstaller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

/**
 * Instalador estilo AAAD: instala las apps declarando
 * com.android.vending como instalador para que Android Auto las muestre.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val ASSET_AUTO = "YouTubeAuto-v1.2-debug.apk"
        const val ASSET_MIRROR = "YouTubeMirror-v1.0.apk"
        const val SPOOFED_INSTALLER = "com.android.vending"
    }

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.statusText)
        findViewById<Button>(R.id.installButton).setOnClickListener { installApp(ASSET_AUTO) }
        findViewById<Button>(R.id.installMirrorButton).setOnClickListener { installApp(ASSET_MIRROR) }
    }

    private fun installApp(assetName: String) {
        try {
            status.setText(R.string.status_copying)
            val outFile = File(cacheDir, assetName)
            assets.open(assetName).use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
            val uri: Uri = FileProvider.getUriForFile(
                this,
                packageName + ".fileprovider",
                outFile
            )
            status.setText(R.string.status_launching)
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, SPOOFED_INSTALLER)
                putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            }
            startActivity(intent)
        } catch (e: Exception) {
            status.setText(R.string.status_error)
        }
    }
}
