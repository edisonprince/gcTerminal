package com.edison.gcterminal.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    fun isUserActivated(): Boolean {
        // Replace with your actual logic to determine if the user is activated
        // This is a placeholder example
        return true // or false based on your logic
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}