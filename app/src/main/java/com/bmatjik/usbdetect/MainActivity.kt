package com.bmatjik.usbdetect

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.bmatjik.usbdetect.databinding.ActivityMainBinding
import com.bmatjik.usbdetect.service.USB_STATUS
import com.bmatjik.usbdetect.service.UsbConnectionCallback
import com.bmatjik.usbdetect.service.UsbPlugReceiver
import com.bmatjik.usbdetect.service.dataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var usbPlugReceiver :UsbPlugReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.UMS_CONNECTED")
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED")
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
        intentFilter.addAction("android.hardware.usb.action.USB_STATE")
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED")

        lifecycleScope.launch {
            dataStore.data.map {p->
                Log.d(Companion.TAG, "onCreate: ${p[USB_STATUS]} ")
                p[USB_STATUS] ?:false
            }.collectLatest {
                if (it){
                    binding.tvStatus.text = "Usb : onConnected"
                }else{
                    binding.tvStatus.text = "Usb : onDisconnected"
                }
            }
        }
//        usbPlugReceiver= UsbPlugReceiver(object : UsbConnectionCallback{
//            override fun onConnected() {
//                binding.tvStatus.text = "Usb : onConnected"
//
//            }
//
//            override fun onDisconnected() {
//                binding.tvStatus.text = "Usb : onDisconnected"
//            }
//
//        })
//
//        registerReceiver(usbPlugReceiver, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(usbPlugReceiver)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}