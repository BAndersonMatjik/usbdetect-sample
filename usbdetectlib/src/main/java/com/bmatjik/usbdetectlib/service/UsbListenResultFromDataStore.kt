package com.bmatjik.usbdetectlib.service

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UsbListenResultFromDataStore(private val context: Context, private val coroutineScope: CoroutineScope, private val usbConnectionCallback: UsbConnectionCallback){
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