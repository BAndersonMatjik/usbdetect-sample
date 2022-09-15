package com.bmatjik.usbdetectlib.service

import android.content.Context
import android.content.IntentFilter
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val usbIntentFilter = IntentFilter().apply {
    addAction("android.intent.action.UMS_CONNECTED")
    addAction("android.intent.action.UMS_DISCONNECTED")
    addAction("android.intent.action.ACTION_POWER_CONNECTED")
    addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
    addAction("android.hardware.usb.action.USB_STATE")
    addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")
}

internal val USB_STATUS = booleanPreferencesKey("usb_status")
internal val Context.dataStore by preferencesDataStore("Setting")