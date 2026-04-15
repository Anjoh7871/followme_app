package com.example.followme02.screen.profile

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val currentLevel: Int = 1,
    val totalPoints: Int = 0,
    val totalAccumulatedKm: Double = 0.0,
    val workouts: Int = 0,
    val streakDays: Int = 0,
    val longestStreak: Int = 0,
    val teamName: String = "",
    val favoriteActivity: String = "",
    val location: String = "",
    val memberSince: String = "",
    val goalCurrentKm: Float = 0f,
    val goalTargetKm: Float = 500f
)