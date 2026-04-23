package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.AchievementRepository
import com.example.followme02.data.repository.AuthRepository
import com.example.followme02.data.repository.UserRepository
import com.example.followme02.data.repository.WorkoutRepository
import com.example.followme02.screen.profile.ProfileUiState
import com.example.followme02.screen.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // ---------------- NOTIFICATIONS ----------------
    fun setNotificationActivity(enabled: Boolean) {
        _uiState.update { it.copy(notificationsActivity = enabled) }
    }

    fun setNotificationFriendRequest(enabled: Boolean) {
        _uiState.update { it.copy(notificationsFriendRequest = enabled) }
    }

    fun setNotificationAchievements(enabled: Boolean) {
        _uiState.update { it.copy(notificationsAchievements = enabled) }
    }

    // ---------------- LANGUAGE ----------------
    fun setLanguage(lang: String) {
        _uiState.update { it.copy(language = lang) }
    }

    // ---------------- BLOCK USERS ----------------
    fun unblockUser(userId: String) {
        _uiState.update {
            it.copy(
                //blockedUsers = it.blockedUsers.filter { u -> u.id != userId }
            )
        }
    }

    // ---------------- ACCOUNT ----------------
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun deleteUser() {
        viewModelScope.launch {

            val profileDeleted = userRepository.deleteUserProfile()
            val authDeleted    = authRepository.deleteAuthUser()

            if (profileDeleted && authDeleted) {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete user")
                }
            }
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            val success = userRepository.updateUsername(newName)

            if (success) {
                _uiState.update {
                    it.copy()
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update username")
                }
            }
        }
    }
}