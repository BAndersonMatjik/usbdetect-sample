package com.bmatjik.usbdetectlib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Deprecated("Heavy Load")
class UsbPluginService : Service(), CoroutineScope {
    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        super.onDestroy()
        coroutineJob.cancel()
    }

    companion object {
        private const val TAG = "UsbPluginService"
    }
}