package com.edison.gcterminal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var database: DatabaseReference
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        FirebaseApp.initializeApp(this@LoginActivity)
        Log.d("LoginActivity", "Firebase initialized")
        // Initialize UI elements
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.btn_login)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("users")
        testDatabaseQuery()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                authenticateUser(username, password)
            } else {
                Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun testDatabaseQuery() {
        if (this::database.isInitialized) {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("LoginActivity", "DataSnapshot: ${dataSnapshot.value}")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("LoginActivity", "Database error: ${databaseError.message}", databaseError.toException())
                }
            })
        } else {
            Log.e("LoginActivity", "Database reference is not initialized")
        }
    }


    private fun authenticateUser(username: String, password: String) {
        Log.d("LoginActivity", "Starting authentication for username: $username")

        database.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("LoginActivity", "Query executed, data found: ${dataSnapshot.value}")

                        for (userSnapshot in dataSnapshot.children) {
                            val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                            Log.d("LoginActivity", "Stored password: $storedPassword")

                            if (storedPassword == password) {
                                // Login success, navigate to MainActivity
                                currentUserId = userSnapshot.key
                                Log.d("LoginActivity", "User ID: $currentUserId")

                                // Generate and store FCM token after successful login
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result
                                        Log.d("LoginActivity", "FCM Token: $token")
                                        storeTokenInDatabase(token)
                                    } else {
                                        Log.w("LoginActivity", "Fetching FCM token failed", task.exception)
                                    }
                                }

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Password doesn't match
                                Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // User not found
                        Toast.makeText(this@LoginActivity, "User does not exist", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("LoginActivity", "Database error: ${databaseError.message}", databaseError.toException())
                    Toast.makeText(this@LoginActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun storeTokenInDatabase(token: String?) {
        if (currentUserId != null && token != null) {
            database.child(currentUserId!!).child("fcmToken").setValue(token)
                .addOnSuccessListener {
                    Log.d("LoginActivity", "Token stored successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("LoginActivity", "Failed to store token", e)
                }
        } else {
            Log.e("LoginActivity", "User not authenticated or token is null")
        }
    }
}
