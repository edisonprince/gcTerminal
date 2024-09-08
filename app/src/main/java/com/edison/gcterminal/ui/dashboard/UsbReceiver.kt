package com.edison.gcterminal.ui.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.widget.Toast

class UsbReceiver(private val onUsbRemoved: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
            // Notify the activity to go to the home page
            onUsbRemoved()
        }
    }
}
