package com.edison.gcterminal.ui.dashboard

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edison.gcterminal.R
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.util.concurrent.Executors

class TerminalActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private var usbPort: UsbSerialPort? = null

    private lateinit var commandInput: EditText
    private lateinit var terminalTextView: TextView

    private val ACTION_USB_PERMISSION = "com.edison.gcterminal.USB_PERMISSION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)

        commandInput = findViewById(R.id.command_input)
        terminalTextView = findViewById(R.id.output_view)
        val sendButton = findViewById<Button>(R.id.send_button)

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        // Request USB permission
        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (ACTION_USB_PERMISSION == action) {
                    synchronized(this) {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                setupUsbConnection(device)
                            }
                        } else {
                            Toast.makeText(context, "Permission denied for USB device", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        registerReceiver(usbReceiver, IntentFilter(ACTION_USB_PERMISSION))

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Toast.makeText(this, "No USB device found", Toast.LENGTH_SHORT).show()
            return
        }

        val driver = availableDrivers[0]
        val device = driver.device
        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        usbManager.requestPermission(device, permissionIntent)

        sendButton.setOnClickListener {
            val command = commandInput.text.toString()
            if (command.isNotEmpty()) {
                sendCommand(command)
            } else {
                Toast.makeText(this, "Command cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUsbConnection(device: UsbDevice) {
        val driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            Toast.makeText(this, "No driver for USB device", Toast.LENGTH_SHORT).show()
            return
        }

        val port = driver.ports[0] // Most devices have just one port (port 0)
        usbPort = port

        val connection = usbManager.openDevice(driver.device) ?: return
        try {
            port.open(connection)
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE) // Example settings

            // Start IO Manager for reading and writing data
            val ioManager = SerialInputOutputManager(port, object : SerialInputOutputManager.Listener {
                override fun onNewData(data: ByteArray) {
                    runOnUiThread {
                        terminalTextView.append(String(data))
                    }
                }

                override fun onRunError(e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@TerminalActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            Executors.newSingleThreadExecutor().submit(ioManager)
        } catch (e: IOException) {
            Toast.makeText(this, "Error setting up device: ${e.message}", Toast.LENGTH_SHORT).show()
            try {
                port.close()
            } catch (ignored: IOException) {
            }
        }
    }

    private fun sendCommand(command: String) {
        usbPort?.let { port ->
            try {
                port.write(command.toByteArray(), 1000)
                terminalTextView.append("Sent: $command\n")
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to send command: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "USB device not connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            usbPort?.close()
        } catch (e: IOException) {
            // Ignore errors on closing
        }
    }
}
