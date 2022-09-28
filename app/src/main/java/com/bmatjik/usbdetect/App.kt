package com.bmatjik.usbdetect

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

class App:Application(){
    private var service:Intent? = null
    override fun onCreate() {
        super.onCreate()

    }

    override fun onTerminate() {
        super.onTerminate()

    }

    companion object {
        private const val TAG = "App"
    }
}