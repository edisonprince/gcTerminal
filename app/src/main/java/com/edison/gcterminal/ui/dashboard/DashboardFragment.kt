package com.edison.gcterminal.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.edison.gcterminal.databinding.FragmentDashboardBinding
import java.io.BufferedReader
import java.io.InputStreamReader


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up button click listeners
        setupButtonListeners()
        SerialButton()
        WifiButton()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun WifiButton(){
        binding.wifi.setOnClickListener{
            val intent = requireContext().packageManager.getLaunchIntentForPackage("com.termux")
            if (intent != null) {
                startActivity(intent)
            } else {
                // Handle the case where the Termux app is not installed
                Toast.makeText(requireContext(), "Termux is not installed", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun SerialButton() {

        binding.serial.setOnClickListener {
            startActivity(Intent(requireContext(), usb_devices::class.java))
        }
//
//        binding.serial.setOnClickListener {
//            checkUserActivationAndShowDialog()
//        }
    }

    private fun checkUserActivationAndShowDialog() {
//        if (!dashboardViewModel.isUserActivated()) {
            showActivationDialog()
//        }
    }

    private fun showActivationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Activate Account")
        builder.setMessage("Your account needs to be activated. Please activate your account to continue.")
        builder.setPositiveButton("Activate") { dialog, _ ->
            // Handle activation process here
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }



    private fun setupButtonListeners() {
        binding.bluetooth.setOnClickListener {
//            executeAdbCommands()
        }
    }

    private fun executeAdbCommands() {
        // Replace with your Android device IP address
        val ipAddress = "192.168.1.2"
        val connectCommand = "adb connect $ipAddress"
        val shellCommand = "adb -s $ipAddress shell input keyevent 26" // Example command
        val disconnectCommand = "adb disconnect $ipAddress"

        try {
            val connectResult = executeShellCommand(connectCommand)
            val shellResult = executeShellCommand(shellCommand)
            val disconnectResult = executeShellCommand(disconnectCommand)

            Toast.makeText(requireContext(), "Connect Result: $connectResult", Toast.LENGTH_LONG).show()
            Toast.makeText(requireContext(), "Shell Result: $shellResult", Toast.LENGTH_LONG).show()
            Toast.makeText(requireContext(), "Disconnect Result: $disconnectResult", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun executeShellCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            process.waitFor()
            output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error executing command: ${e.message}"
        }
    }
}
