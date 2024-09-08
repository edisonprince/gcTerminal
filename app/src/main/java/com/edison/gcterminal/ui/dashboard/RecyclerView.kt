//package com.edison.gcterminal.ui.dashboard
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Spinner
//import android.widget.TextView
//import androidx.appcompat.app.AlertDialog
//import androidx.recyclerview.widget.RecyclerView
//import android.hardware.usb.UsbDevice
//import com.edison.gcterminal.R
//
//class UsbDeviceAdapter(private val context: Context, private val usbDeviceList: List<UsbDevice>) :
//    RecyclerView.Adapter<UsbDeviceAdapter.UsbDeviceViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsbDeviceViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.usb_device_item, parent, false)
//        return UsbDeviceViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UsbDeviceViewHolder, position: Int) {
//        val usbDevice = usbDeviceList[position]
//        holder.deviceName.text = usbDevice.deviceName
//        holder.deviceInfo.text = "Vendor ID: ${usbDevice.vendorId}, Product ID: ${usbDevice.productId}"
//
//        holder.itemView.setOnClickListener {
//            showDeviceOptionsDialog(usbDevice)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return usbDeviceList.size
//    }
//
//    @SuppressLint("InflateParams")
//    private fun showDeviceOptionsDialog(usbDevice: UsbDevice) {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_usb_device, null)
//        val spinner1: Spinner = dialogView.findViewById(R.id.dialog_spinner)
//        val spinner2: Spinner = dialogView.findViewById(R.id.dialog_spinner2)
//
//        // Define static data for the spinners
//        val options1 = arrayOf("MSTAR", "Mediatek", "REALTEK", "HISILICON", "PANASONIC", "AMLOGIC","Fire TV")
//        val adapter1 = ArrayAdapter(context, android.R.layout.simple_spinner_item, options1)
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner1.adapter = adapter1
//
//        val options2 = arrayOf("9600", "19200", "38400", "115200", "230400", "460800", "921600", "386000")
//        val adapter2 = ArrayAdapter(context, android.R.layout.simple_spinner_item, options2)
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner2.adapter = adapter2
//
//        AlertDialog.Builder(context)
//            .setTitle("USB Device Options")
//            .setView(dialogView)
//            .setPositiveButton("Connect") { dialog, _ ->
//                val connectionType = spinner1.selectedItem.toString()
//                val baudRate = spinner2.selectedItem.toString()
//                val intent = Intent(context, TerminalActivity::class.java).apply {
//                    putExtra("connectionType", connectionType)
//                    putExtra("baudRate", baudRate)
//                }
//                context.startActivity(intent)
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    class UsbDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val deviceName: TextView = itemView.findViewById(R.id.device_name)
//        val deviceInfo: TextView = itemView.findViewById(R.id.device_info)
//    }
//}
package com.edison.gcterminal.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.hardware.usb.UsbDevice
import com.edison.gcterminal.R

class UsbDeviceAdapter(private val context: Context, private var usbDeviceList: List<UsbDevice>) :
    RecyclerView.Adapter<UsbDeviceAdapter.UsbDeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsbDeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usb_device_item, parent, false)
        return UsbDeviceViewHolder(view)
    }
    private lateinit var usbReceiver: UsbReceiver
    override fun onBindViewHolder(holder: UsbDeviceViewHolder, position: Int) {
        val usbDevice = usbDeviceList[position]
        holder.deviceName.text = usbDevice.deviceName
        holder.deviceInfo.text = "Vendor ID: ${usbDevice.vendorId}, Product ID: ${usbDevice.productId}"

        holder.itemView.setOnClickListener {
            showDeviceOptionsDialog(usbDevice)
        }
    }

    override fun getItemCount(): Int {
        return usbDeviceList.size
    }

    @SuppressLint("InflateParams")
    private fun showDeviceOptionsDialog(usbDevice: UsbDevice) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_usb_device, null)
        val spinner1: Spinner = dialogView.findViewById(R.id.dialog_spinner)
        val spinner2: Spinner = dialogView.findViewById(R.id.dialog_spinner2)

        // Define static data for the spinners
        val options1 = arrayOf("MSTAR", "Mediatek", "REALTEK", "HISILICON", "PANASONIC", "AMLOGIC", "Fire TV")
        val adapter1 = ArrayAdapter(context, android.R.layout.simple_spinner_item, options1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        val options2 = arrayOf("9600", "19200", "38400", "115200", "230400", "460800", "921600", "386000")
        val adapter2 = ArrayAdapter(context, android.R.layout.simple_spinner_item, options2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        AlertDialog.Builder(context)
            .setTitle("USB Device Options")
            .setView(dialogView)
            .setPositiveButton("Connect") { dialog, _ ->
                val connectionType = spinner1.selectedItem.toString()
                val baudRate = spinner2.selectedItem.toString()
                val intent = Intent(context, TerminalActivity::class.java).apply {
                    putExtra("connectionType", connectionType)
                    putExtra("baudRate", baudRate)
                }
                context.startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun updateData(newDeviceList: List<UsbDevice>) {
        usbDeviceList = newDeviceList
        notifyDataSetChanged()  // Notify RecyclerView that data has changed
    }

    class UsbDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val deviceInfo: TextView = itemView.findViewById(R.id.device_info)
    }
}
