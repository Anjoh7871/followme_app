package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.SocialRepository
import com.example.followme02.screen.social.LeaderboardType
import com.example.followme02.screen.social.SearchRelationshipStatus
import com.example.followme02.screen.social.SocialTab
import com.example.followme02.screen.social.SocialUiState
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {

    private val repository = SocialRepository()

    var uiState = mutableStateOf(SocialUiState())
        private set

    var availableDestinations = mutableStateOf<List<Pair<Int, String>>>(emptyList())
        private set

    fun loadSocialData() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val friends = repository.getFriends()
                val friendRequests = repository.getFriendRequests()
                val currentTeam = repository.getCurrentTeam()
                val teamMembers = currentTeam?.let { repository.getTeamMembers(it.teamId) } ?: emptyList()
                val availableTeams = repository.getAllTeams()
                val destinations = repository.getAvailableDestinations()

                availableDestinations.value = destinations

                uiState.value = uiState.value.copy(
                    isLoading = false,
                    friends = friends,
                    friendRequests = friendRequests,
                    currentTeam = currentTeam,
                    teamMembers = teamMembers,
                    availableTeams = availableTeams
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun onTabSelected(tab: SocialTab) {
        uiState.value = uiState.value.copy(selectedTab = tab)
    }

    fun onLeaderboardTypeSelected(type: LeaderboardType) {
        uiState.value = uiState.value.copy(leaderboardType = type)
    }

    fun onFriendSearchQueryChange(query: String) {
        uiState.value = uiState.value.copy(
            friendSearchQuery = query,
            friendSearchResult = null,
            friendSearchMessage = null
        )
    }

    fun onTeamSearchQueryChange(query: String) {
        uiState.value = uiState.value.copy(teamSearchQuery = query)
    }

    fun onCreateTeamNameChange(query: String) {
        uiState.value = uiState.value.copy(createTeamName = query)
    }

    fun searchUserByEmail() {
        viewModelScope.launch {
            val query = uiState.value.friendSearchQuery.trim()

            if (query.isBlank()) {
                uiState.value = uiState.value.copy(
                    friendSearchResult = null,
                    friendSearchMessage = "Write an email first."
                )
                return@launch
            }

            uiState.value = uiState.value.copy(
                isLoading = true,
                friendSearchResult = null,
                friendSearchMessage = null
            )

            try {
                val result = repository.searchUserByEmail(query)

                uiState.value = uiState.value.copy(
                    isLoading = false,
                    friendSearchResult = result,
                    friendSearchMessage = if (result == null) {
                        "No user found with that email or username."
                    } else {
                        null
                    }
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    friendSearchResult = null,
                    friendSearchMessage = e.message ?: "Search failed."
                )
            }
        }
    }

    fun sendFriendRequest(targetUserId: Int) {
        viewModelScope.launch {
            repository.sendFriendRequest(targetUserId)
            loadSocialData()

            if (uiState.value.friendSearchQuery.isNotBlank()) {
                searchUserByEmail()
            }
        }
    }

    fun acceptFriendRequest(requesterUserId: Int) {
        viewModelScope.launch {
            repository.acceptFriendRequest(requesterUserId)
            loadSocialData()

            val currentResult = uiState.value.friendSearchResult
            if (currentResult != null && currentResult.userId == requesterUserId) {
                searchUserByEmail()
            }
        }
    }

    fun declineFriendRequest(requesterUserId: Int) {
        viewModelScope.launch {
            repository.declineFriendRequest(requesterUserId)
            loadSocialData()

            val currentResult = uiState.value.friendSearchResult
            if (currentResult != null && currentResult.userId == requesterUserId) {
                searchUserByEmail()
            }
        }
    }

    fun createTeam() {
        viewModelScope.launch {
            val teamName = uiState.value.createTeamName.trim()
            if (teamName.isBlank()) return@launch

            repository.createTeam(teamName)

            uiState.value = uiState.value.copy(createTeamName = "")
            loadSocialData()
        }
    }

    fun setTeamJourney(destinationId: Int) {
        viewModelScope.launch {
            repository.setTeamJourney(destinationId)
            loadSocialData()
        }
    }

    fun joinTeam(teamId: Int) {
        viewModelScope.launch {
            repository.joinTeam(teamId)
            loadSocialData()
        }
    }

    fun getFilteredTeams() = uiState.value.availableTeams.filter {
        uiState.value.teamSearchQuery.isBlank() ||
                it.teamName.contains(uiState.value.teamSearchQuery, ignoreCase = true)
    }

    fun getSortedLeaderboard() = when (uiState.value.leaderboardType) {
        LeaderboardType.POINTS -> uiState.value.teamMembers.sortedByDescending { it.totalPoints }
        LeaderboardType.KM -> uiState.value.teamMembers.sortedByDescending { it.totalKm }
    }

    fun getSearchActionLabel(): String {
        val result = uiState.value.friendSearchResult ?: return "Send request"

        return when (result.relationshipStatus) {
            SearchRelationshipStatus.NONE -> "Send request"
            SearchRelationshipStatus.SELF -> "This is you"
            SearchRelationshipStatus.FRIEND -> "Already friends"
            SearchRelationshipStatus.PENDING_SENT -> "Request sent"
            SearchRelationshipStatus.PENDING_RECEIVED -> "Accept request"
        }
    }

    fun isSearchActionEnabled(): Boolean {
        val result = uiState.value.friendSearchResult ?: return false

        return when (result.relationshipStatus) {
            SearchRelationshipStatus.NONE -> true
            SearchRelationshipStatus.PENDING_RECEIVED -> true
            SearchRelationshipStatus.SELF -> false
            SearchRelationshipStatus.FRIEND -> false
            SearchRelationshipStatus.PENDING_SENT -> false
        }
    }

    fun onSearchAction() {
        val result = uiState.value.friendSearchResult ?: return

        when (result.relationshipStatus) {
            SearchRelationshipStatus.NONE -> sendFriendRequest(result.userId)
            SearchRelationshipStatus.PENDING_RECEIVED -> acceptFriendRequest(result.userId)
            else -> Unit
        }
    }
}