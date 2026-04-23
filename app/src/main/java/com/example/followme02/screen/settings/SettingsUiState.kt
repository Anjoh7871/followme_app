package com.example.followme02.screen.settings

import com.example.followme02.model.Users


data class SettingsUiState(

    // Notifications
    val notificationsActivity: Boolean = true,
    val notificationsFriendRequest: Boolean = true,
    val notificationsAchievements: Boolean = true,

    // Language
    val language: String = "NO",

    // Blocked users
    val blockedUsers: List<Users> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)