package com.example.followme02.screen.settings

import com.example.followme02.model.Users


data class SettingsUiState(

    // Language
    val language: String = "EN",

    // Blocked users
    val blockedUsers: List<Users> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)