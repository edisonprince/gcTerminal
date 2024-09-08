package com.edison.gcterminal.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edison.gcterminal.R
import java.io.BufferedReader
import java.io.InputStreamReader

class SimpleTerminalActivity : AppCompatActivity() {

    private lateinit var commandInput: EditText
    private lateinit var runButton: Button
    private lateinit var outputView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal_simple)

        commandInput = findViewById(R.id.commandInput)
        runButton = findViewById(R.id.runButton)
        outputView = findViewById(R.id.outputView)

        runButton.setOnClickListener {
            val command = commandInput.text.toString()
            if (command.isNotEmpty()) {
                executeCommand(command)
            }
        }
    }

    private fun executeCommand(command: String) {
        try {
            // Use ProcessBuilder to run the command
            val process = ProcessBuilder()
                .command(command.split(" "))
                .redirectErrorStream(true)
                .start()

            // Capture the output of the command
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            // Wait for the process to finish
            process.waitFor()

            // Display the output in the TextView
            outputView.text = output.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            outputView.text = e.message
        }
    }
}