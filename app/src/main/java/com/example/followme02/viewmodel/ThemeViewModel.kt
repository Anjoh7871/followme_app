package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.DarkModeRepository
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {

    private val repository = DarkModeRepository()

    var isDarkMode = mutableStateOf(false)
        private set

    fun loadDarkMode() {
        viewModelScope.launch {
            val savedDarkMode = repository.loadDarkModeForCurrentUser()

            if (savedDarkMode != null) {
                isDarkMode.value = savedDarkMode
            }
        }
    }

    fun toggleDarkMode() {
        val newValue = !isDarkMode.value
        isDarkMode.value = newValue

        viewModelScope.launch {
            repository.saveDarkModeForCurrentUser(newValue)
        }
    }

    fun resetToLightMode() {
        isDarkMode.value = false
    }
}