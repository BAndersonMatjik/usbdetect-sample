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

import com.bmatjik.usbdetectlib.service.UsbConnectionCallback
import com.bmatjik.usbdetectlib.service.UsbDetectionObservable
import com.bmatjik.usbdetectlib.service.usbPlugReceiverAsFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),UsbConnectionCallback {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(UsbDetectionObservable(context = this,this))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onConnected() {
        binding.tvStatus.text = "Usb : onConnected"
    }

    override fun onDisconnected() {
        binding.tvStatus.text = "Usb : onDisconnected"
    }

}