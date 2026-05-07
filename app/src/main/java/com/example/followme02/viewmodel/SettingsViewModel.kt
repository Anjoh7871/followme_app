package com.example.followme02.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.AuthRepository
import com.example.followme02.data.repository.SocialRepository
import com.example.followme02.data.repository.UserRepository
import com.example.followme02.screen.settings.SettingsUiState
import com.example.followme02.screen.social.SocialFriendUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()
    private val socialRepository = SocialRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // ---------------- LANGUAGE ----------------

    fun loadLanguage(context: Context) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("lang", "EN") ?: "EN"
        _uiState.update {
            it.copy(language = savedLang)
        }

    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(language = lang) }
    }

    // ---------------- BLOCK USERS ----------------

    fun loadBlockedUsers() {

        viewModelScope.launch {
            val blocked: List<SocialFriendUi> = socialRepository.getBlockedUsers()
            Log.d("BLOCK_DEBUG", "ALL blocked = $blocked")
            _uiState.update {
                it.copy(
                    blockedUsers = blocked
                )
            }
        }

    }

    fun blockUser(targetUserId: Int) {

        viewModelScope.launch {

            socialRepository.blockUser(targetUserId)
            val updated = socialRepository.getBlockedUsers()

            _uiState.update {
                it.copy(blockedUsers = updated)
            }
        }
    }

    fun unblockUser(targetUserId: Int) {

        viewModelScope.launch {

            socialRepository.unblockUser(targetUserId)
            val updated = socialRepository.getBlockedUsers()

            _uiState.update {
                it.copy(blockedUsers = updated)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResult = null,
                searchMessage = null
            )
        }
    }

    fun searchUserByEmail() {

        viewModelScope.launch {

            val query = _uiState.value.searchQuery.trim()

            if (query.isBlank()) {
                _uiState.update {
                    it.copy(
                        searchResult = null,
                        searchMessage = "Write an email first."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    searchResult = null,
                    searchMessage = null
                )
            }

            try {
                val result = socialRepository.searchUserByEmail(query)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchResult = result,
                        searchMessage = if (result == null) {
                            "No user found with that email or username."
                        } else {
                            null
                        }
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchResult = null,
                        searchMessage = e.message ?: "Search failed."
                    )
                }
            }
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