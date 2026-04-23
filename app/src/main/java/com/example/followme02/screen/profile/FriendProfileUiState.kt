package com.example.followme02.screen.profile

import com.example.followme02.model.JourneyUiModel

data class FriendProfileHeaderUi(
    val userId: Int = 0,
    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalKm: Double = 0.0
)

data class FriendCurrentJourneyUi(
    val destinationId: Int? = null,
    val destinationName: String = "No active journey",
    val progressKm: Double = 0.0,
    val targetKm: Double = 0.0,
    val progressFraction: Float = 0f,
    val hasActiveJourney: Boolean = false
)

data class FriendProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val friend: FriendProfileHeaderUi? = null,
    val achievementsCount: Int = 0,
    val currentJourney: FriendCurrentJourneyUi = FriendCurrentJourneyUi(),
    val completedJourneys: List<JourneyUiModel> = emptyList(),
    val friendRemoved: Boolean = false
)