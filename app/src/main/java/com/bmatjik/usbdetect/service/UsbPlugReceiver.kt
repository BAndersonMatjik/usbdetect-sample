package com.bmatjik.usbdetect.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bmatjik.usbdetect.BuildConfig

class UsbPlugReceiver(private val usbConnectionCallback:UsbConnectionCallback) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (BuildConfig.DEBUG){
            Log.i("UsbPlugReceiver", "action: " + action);
        }
        action?.apply {
            if (this.equals("android.intent.action.UMS_CONNECTED", true)) {
                usbConnectionCallback.onConnected()
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (action == "android.hardware.usb.action.USB_STATE") {
                    if (intent.extras?.getBoolean("connected") == true) {
                        usbConnectionCallback.onConnected()
                    } else {
                        usbConnectionCallback.onDisconnected()
                    }
                }
            } else {
                if (action == Intent.ACTION_POWER_CONNECTED) {
                    usbConnectionCallback.onConnected()
                } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
                    usbConnectionCallback.onDisconnected()
                }
            }
        }
    }
}

interface UsbConnectionCallback{
    fun onConnected()
    fun onDisconnected()
}