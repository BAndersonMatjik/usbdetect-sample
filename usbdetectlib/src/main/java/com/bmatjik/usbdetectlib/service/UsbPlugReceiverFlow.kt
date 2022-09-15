package com.bmatjik.usbdetectlib.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalCoroutinesApi::class)
fun usbPlugReceiverFlow(context: Context) = callbackFlow<Boolean> {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            action?.apply {
                if (this.equals("android.intent.action.UMS_CONNECTED", true)) {
                    trySendBlocking(true)
                    return
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (action == "android.hardware.usb.action.USB_STATE") {
                        if (intent.extras?.getBoolean("connected") == true) {
                            trySendBlocking(true)
                        } else {
                            trySendBlocking(false)
                        }
                    }
                } else {
                    if (action == Intent.ACTION_POWER_CONNECTED) {
                        trySendBlocking(true)
                    } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
                        trySendBlocking(false)
                    }
                }
            }
        }

    }
    context.registerReceiver(receiver, usbIntentFilter)
    awaitClose {
        Log.d("usbFlow", "UsbPlugReceiverFlow: CloseFlow")
        context.unregisterReceiver(receiver)
    }
}