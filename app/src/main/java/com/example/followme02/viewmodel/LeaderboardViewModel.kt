package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.LeaderboardRepository
import com.example.followme02.data.repository.LeaderboardUser
import com.example.followme02.data.repository.UserRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    private val repository = LeaderboardRepository()

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    val leaderboardUsers = mutableStateListOf<LeaderboardUser>()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val result = repository.getLeaderboardUsers()
                leaderboardUsers.clear()
                leaderboardUsers.addAll(result)
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }
}