package com.edison.gcterminal.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class UsbDeviceDialogFragment(private val usbDeviceName: String, private val usbDeviceInfo: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("USB Device Details")
            .setMessage("Device Name: $usbDeviceName\n$usbDeviceInfo")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}
