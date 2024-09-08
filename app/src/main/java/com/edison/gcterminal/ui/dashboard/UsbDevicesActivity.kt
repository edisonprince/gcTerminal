//package com.edison.gcterminal.ui.dashboard
//
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.hardware.usb.UsbDevice
//import android.hardware.usb.UsbManager
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.edison.gcterminal.R
//
//class UsbDevicesActivity : AppCompatActivity() {
//
//    private lateinit var usbManager: UsbManager
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var usbDeviceAdapter: UsbDeviceAdapter
//    private val permissionIntent: PendingIntent by lazy {
//        PendingIntent.getBroadcast(
//            this, 0, Intent(ACTION_USB_PERMISSION), 0
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.usb_devices)
//
//        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
//        recyclerView = findViewById(R.id.usb_devices_recycler_view)
//
//        val deviceList = usbManager.deviceList.values.toList()
//        usbDeviceAdapter = UsbDeviceAdapter(this, deviceList) { usbDevice ->
//            requestUsbPermission(usbDevice)
//        }
//
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@UsbDevicesActivity)
//            adapter = usbDeviceAdapter
//        }
//
//        // Register the BroadcastReceiver for USB permission
//        val filter = IntentFilter(ACTION_USB_PERMISSION)
//        registerReceiver(usbReceiver, filter)
//    }
//
//    private fun requestUsbPermission(device: UsbDevice) {
//        usbManager.requestPermission(device, permissionIntent)
//    }
//
//    private val usbReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                ACTION_USB_PERMISSION -> synchronized(this) {
//                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
//
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        device?.let {
//                            // Permission granted, proceed with communication
//                            Toast.makeText(this@UsbDevicesActivity, "Permission granted for ${device.deviceName}", Toast.LENGTH_SHORT).show()
//                            // Start communication with the device
//                        }
//                    } else {
//                        Toast.makeText(this@UsbDevicesActivity, "Permission denied for ${device?.deviceName}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(usbReceiver)
//    }
//
//    companion object {
//        const val ACTION_USB_PERMISSION = "com.edison.gcterminal.USB_PERMISSION"
//    }
//}
