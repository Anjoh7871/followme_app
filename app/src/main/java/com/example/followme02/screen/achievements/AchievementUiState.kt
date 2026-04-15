package com.example.followme02.screen.achievements

data class AchievementUiState(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Float, // 0.0 to 1.0
    val progressLabel: String, // e.g., "6.5 / 10"
    val unlockedDate: String? = null
)
