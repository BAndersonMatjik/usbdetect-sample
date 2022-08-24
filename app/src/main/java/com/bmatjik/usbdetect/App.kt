package com.bmatjik.usbdetect

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bmatjik.usbdetectlib.service.UsbPluginService
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

class App:Application() {
    private var service:Intent? = null
    override fun onCreate() {
        super.onCreate()
//        Intent(this, UsbPluginService::class.java).also { intent ->
//            service = intent
//            startService(intent)
//        }
    }

    override fun onTerminate() {
        super.onTerminate()
//        service?.apply {
//            stopService(this)
//        }
    }

    companion object {
        private const val TAG = "App"
    }
}