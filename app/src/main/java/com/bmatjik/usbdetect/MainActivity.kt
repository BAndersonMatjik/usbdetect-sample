package com.bmatjik.usbdetect

import android.content.IntentFilter
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.bmatjik.usbdetect.databinding.ActivityMainBinding
import com.bmatjik.usbdetect.service.UsbConnectionCallback
import com.bmatjik.usbdetect.service.UsbPlugReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usbPlugReceiver :UsbPlugReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.ums_connected")
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED")
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
        intentFilter.addAction("android.hardware.usb.action.USB_STATE")
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")

        usbPlugReceiver= UsbPlugReceiver(object : UsbConnectionCallback{
            override fun onConnected() {
                binding.tvStatus.text = "Usb : onConnected"

            }

            override fun onDisconnected() {
                binding.tvStatus.text = "Usb : onDisconnected"
            }

        })

        registerReceiver(usbPlugReceiver, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbPlugReceiver)
    }

}