package com.bmatjik.usbdetectlib.service

import android.content.BroadcastReceiver
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class UsbDetectionObservable(
    private val context: Context,
    private val usbConnectionCallback: UsbConnectionCallback
) : DefaultLifecycleObserver {
    private var usbReceiver: BroadcastReceiver? = null
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        usbReceiver = UsbPlugReceiver(usbConnectionCallback)
        usbReceiver?.apply {
            context.registerReceiver(this, usbIntentFilter)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        context.unregisterReceiver(usbReceiver)
    }
}