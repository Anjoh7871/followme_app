package com.example.followme02.screen.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var loginMessage = mutableStateOf<String?>(null)
        private set

    var registerMessage = mutableStateOf<String?>(null)
        private set

    var currentUser = mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val success = repository.login(email, password)

            if (success) {
                currentUser.value = email
                loginMessage.value = "Login successful"
            } else {
                loginMessage.value = "Invalid username or password"
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            val success = repository.register(username, email, password)

            if (success) {
                registerMessage.value = "User registered"
            } else {
                registerMessage.value = "Registration failed"
            }
        }
    }

    fun clearLoginMessage() {
        loginMessage.value = null
    }

    fun clearRegisterMessage() {
        registerMessage.value = null
    }

    fun clearAuthState() {
        loginMessage.value = null
        registerMessage.value = null
        currentUser.value = null
    }
}