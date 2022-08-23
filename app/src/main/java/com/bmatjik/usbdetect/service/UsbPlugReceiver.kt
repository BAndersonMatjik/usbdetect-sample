package com.bmatjik.usbdetect.service

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bmatjik.usbdetect.BuildConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import java.util.prefs.Preferences


fun usbPlugReceiverFlow(context: Context) = callbackFlow<Boolean> {
    val receiver =  object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BuildConfig.DEBUG){
                Log.i("UsbPlugReceiver", "action: " + action);
            }
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
    val intentFilter = IntentFilter()
    intentFilter.addAction("android.intent.action.UMS_CONNECTED")
    intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED")
    intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
    intentFilter.addAction("android.hardware.usb.action.USB_STATE")
    intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")
    context.registerReceiver(receiver, intentFilter)
    awaitClose {
        Log.d("usbFlow", "UsbPlugReceiverFlow: CloseFlow")
        context.unregisterReceiver(receiver)
    }
}



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
val USB_STATUS = booleanPreferencesKey("usb_status")
val Context.dataStore by preferencesDataStore("Setting")
