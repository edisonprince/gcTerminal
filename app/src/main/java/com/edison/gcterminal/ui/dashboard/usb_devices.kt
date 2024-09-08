//package com.edison.gcterminal.ui.dashboard
//
//import android.hardware.usb.UsbManager
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.edison.gcterminal.R
//
//class usb_devices : AppCompatActivity() {
//
//    private lateinit var usbManager: UsbManager
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var usbDeviceAdapter: UsbDeviceAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.usb_devices)
//
//        usbManager = getSystemService(USB_SERVICE) as UsbManager
//        recyclerView = findViewById(R.id.usb_devices_recycler_view)
//
//        val deviceList = usbManager.deviceList.values.toList()
//        usbDeviceAdapter = UsbDeviceAdapter(this, deviceList)
//
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@usb_devices)
//            adapter = usbDeviceAdapter
//        }
//    }
//}

package com.edison.gcterminal.ui.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edison.gcterminal.R

class usb_devices : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var usbDeviceAdapter: UsbDeviceAdapter

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    // A USB device has been attached
                    updateDeviceList()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    // A USB device has been detached
                    updateDeviceList()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usb_devices)

        usbManager = getSystemService(USB_SERVICE) as UsbManager
        recyclerView = findViewById(R.id.usb_devices_recycler_view)

        usbDeviceAdapter = UsbDeviceAdapter(this, emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@usb_devices)
            adapter = usbDeviceAdapter
        }

        // Register the BroadcastReceiver to listen for USB device attach/detach events
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(usbReceiver, filter)

        // Initial load of USB devices
        updateDeviceList()
    }

    private fun updateDeviceList() {
        val deviceList = usbManager.deviceList.values.toList()
        usbDeviceAdapter.updateData(deviceList)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(usbReceiver)
    }
}
