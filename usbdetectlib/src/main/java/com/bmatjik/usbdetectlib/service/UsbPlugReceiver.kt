package com.bmatjik.usbdetectlib.service

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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bmatjik.usbdetectlib.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

internal val usbIntentFilter = IntentFilter().apply {
    addAction("android.intent.action.UMS_CONNECTED")
    addAction("android.intent.action.UMS_DISCONNECTED")
    addAction("android.intent.action.ACTION_POWER_CONNECTED")
    addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
    addAction("android.hardware.usb.action.USB_STATE")
    addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")
}


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
    context.registerReceiver(receiver, usbIntentFilter)
    awaitClose {
        Log.d("usbFlow", "UsbPlugReceiverFlow: CloseFlow")
        context.unregisterReceiver(receiver)
    }
}


class UsbDetectionObservable(private val context: Context):DefaultLifecycleObserver{
    private var intents:Intent? = null
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        Intent(context, UsbPluginService::class.java).also { intent ->
            intents = intent
            context.startService(intent)
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        intents?.apply {
            context.stopService(this)
        }
    }
}

internal class UsbPlugReceiver(private val usbConnectionCallback:UsbConnectionCallback) : BroadcastReceiver() {
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

class UsbListenResultFromDataStore(private val context: Context,private val coroutineScope: CoroutineScope,private val usbConnectionCallback: UsbConnectionCallback){
    init {
        coroutineScope.launch {
            context.dataStore.data.map {p->
                p[USB_STATUS] ?:false
            }.collectLatest {
                if (it){
                    usbConnectionCallback.onConnected()
                }else{
                    usbConnectionCallback.onDisconnected()
                }
            }
        }
    }
}

internal val USB_STATUS = booleanPreferencesKey("usb_status")
internal val Context.dataStore by preferencesDataStore("Setting")
