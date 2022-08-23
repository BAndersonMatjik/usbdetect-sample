package com.bmatjik.usbdetect.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UsbPluginService : Service(), CoroutineScope {
    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Companion.TAG, "onStartCommand: start")
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        launch {
            usbPlugReceiverFlow(this@UsbPluginService).collectLatest { status ->
                dataStore.edit {
                    it[USB_STATUS] = status
                }
            }
        }
        return Service.START_NOT_STICKY
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        coroutineJob.cancel()
    }

    companion object {
        private const val TAG = "UsbPluginService"
    }
}