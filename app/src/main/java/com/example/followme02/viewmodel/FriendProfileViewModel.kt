package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.FriendProfileRepository
import com.example.followme02.screen.profile.FriendCurrentJourneyUi
import com.example.followme02.screen.profile.FriendProfileHeaderUi
import com.example.followme02.screen.profile.FriendProfileUiState
import kotlinx.coroutines.launch

class FriendProfileViewModel : ViewModel() {

    private val repository = FriendProfileRepository()

    var uiState = mutableStateOf(FriendProfileUiState())
        private set

    fun loadFriendProfile(friendUserId: Int) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                friendRemoved = false
            )

            try {
                val friend = repository.getFriendUser(friendUserId)

                if (friend == null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not find this user."
                    )
                    return@launch
                }

                val achievementsCount = repository.getAchievementsCount(friendUserId)
                val completedJourneys = repository.getCompletedJourneys(friendUserId)

                val currentDestination = friend.selectedDestinationId?.let {
                    repository.getDestinationById(it)
                }

                val progressKm = if (
                    friend.selectedDestinationId != null &&
                    currentDestination != null
                ) {
                    (friend.totalAccumulatedKm - (friend.journeyStartKm ?: 0.0)).coerceAtLeast(0.0)
                } else {
                    0.0
                }

                val targetKm = currentDestination?.kmThreshold ?: 0.0

                val progressFraction = if (targetKm > 0.0) {
                    (progressKm / targetKm).coerceIn(0.0, 1.0).toFloat()
                } else {
                    0f
                }

                uiState.value = FriendProfileUiState(
                    isLoading = false,
                    friend = FriendProfileHeaderUi(
                        userId = friend.userId,
                        username = friend.username,
                        email = friend.email,
                        avatarUrl = friend.avatarUrl,
                        level = friend.levelId,
                        totalPoints = friend.totalPoints,
                        totalKm = friend.totalAccumulatedKm
                    ),
                    achievementsCount = achievementsCount,
                    currentJourney = FriendCurrentJourneyUi(
                        destinationId = currentDestination?.destinationId,
                        destinationName = currentDestination?.name ?: "No active journey",
                        progressKm = progressKm,
                        targetKm = targetKm,
                        progressFraction = progressFraction,
                        hasActiveJourney = currentDestination != null
                    ),
                    completedJourneys = completedJourneys
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun removeFriend(friendUserId: Int) {
        viewModelScope.launch {
            val success = repository.removeFriend(friendUserId)

            if (success) {
                uiState.value = uiState.value.copy(friendRemoved = true)
            } else {
                uiState.value = uiState.value.copy(
                    errorMessage = "Failed to remove friend."
                )
            }
        }
    }
}