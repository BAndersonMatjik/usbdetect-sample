package com.bmatjik.usbdetectlib.service

interface UsbConnectionCallback{
    fun onConnected()
    fun onDisconnected()
}