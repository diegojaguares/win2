package com.example.youtubesdl

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubesdl.databinding.ActivityMainBinding
import com.smartdevicelink.managers.CompletionListener
import com.smartdevicelink.managers.SdlManager
import com.smartdevicelink.managers.SdlManagerListener
import com.smartdevicelink.managers.lifecycle.LifecycleConfigurationUpdate
import com.smartdevicelink.proxy.rpc.enums.AppHMIType
import com.smartdevicelink.proxy.rpc.enums.Language
import com.smartdevicelink.util.SystemInfo
import com.smartdevicelink.streaming.video.VideoStreamingParameters
import com.smartdevicelink.transport.MultiplexTransportConfig
import java.util.Vector

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var sdlManager: SdlManager? = null

    companion object {
        const val APP_ID = "7d9b4c2a-1e5f-4a8b-9c0d-3e6f7a8b9c0d"
        const val APP_NAME = "YouTube SDL"
        const val DEFAULT_VIDEO = "jfKfPfyJRdk"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.connectButton.setOnClickListener { toggle() }
    }

    private fun toggle() {
        if (sdlManager == null) { connect() } else { disconnect() }
    }

    private fun connect() {
        setStatus("Conectando al carro...")
        val transport = MultiplexTransportConfig(this, APP_ID)
        val types = Vector<AppHMIType>()
        types.add(AppHMIType.NAVIGATION)
        val builder = SdlManager.Builder(this, APP_ID, APP_NAME, ManagerListener())
        builder.setTransportType(transport)
        builder.setAppTypes(types)
        sdlManager = builder.build()
        sdlManager?.start()
        binding.connectButton.setText(R.string.disconnect)
    }

    private fun disconnect() {
        try { sdlManager?.dispose() } catch (e: Exception) { }
        sdlManager = null
        setStatus(getString(R.string.status_idle))
        binding.connectButton.setText(R.string.connect)
    }

    private fun setStatus(text: String) {
        runOnUiThread { binding.statusText.text = text }
    }

    private inner class ManagerListener : SdlManagerListener {
        override fun onStart() {
            setStatus("Conectado. Iniciando video...")
            val vsm = sdlManager?.videoStreamManager
            if (vsm == null) {
                setStatus("El carro no ofrece video (manager nulo)")
                return
            }
            vsm.start(object : CompletionListener {
                override fun onComplete(success: Boolean) {
                    if (!success) {
                        setStatus("Video no disponible en este carro")
                        return
                    }
                    vsm.startRemoteDisplayStream(this@MainActivity, CarDisplay::class.java, VideoStreamingParameters(), false)
                    setStatus("Video enviandose al carro")
                }
            })
        }

        override fun onDestroy() {
            setStatus(getString(R.string.status_idle))
        }

        override fun onSystemInfoReceived(systemInfo: SystemInfo?): Boolean {
            return false
        }

        override fun managerShouldUpdateLifecycle(language: Language?, hmiLanguage: Language?): LifecycleConfigurationUpdate? {
            return null
        }

        override fun onError(info: String?, e: Exception?) {
            val msg = info ?: "error desconocido"
            setStatus("Error SDL: " + msg)
        }
    }

    override fun onDestroy() {
        try { sdlManager?.dispose() } catch (e: Exception) { }
        sdlManager = null
        super.onDestroy()
    }
}
