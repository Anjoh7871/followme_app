package com.example.followme02.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.remote.SupabaseProvider.client
import com.example.followme02.data.repository.JourneyRepository
import com.example.followme02.model.Destinations
import com.example.followme02.model.JourneyUiModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import io.github.jan.supabase.postgrest.query.Columns
import com.example.followme02.model.UserIdResponse
import io.github.jan.supabase.auth.auth

suspend fun getCurrentDbUserId(): Int? {
    return try {
        val authId = client.auth.currentUserOrNull()?.id ?: return null

        val result = client
            .from("users")
            .select(columns = Columns.list("user_id")) {
                filter {
                    eq("auth_id", authId)
                }
            }
            .decodeSingleOrNull<UserIdResponse>()

        result?.userId
    } catch (e: Exception) {
        Log.e("JOURNEY_REPO", "Error getting db user id", e)
        null
    }
}

enum class SortType {
    DATE, KM, NAME
}

class JourneyViewModel : ViewModel() {

    private val repository = JourneyRepository()

    var journeys = mutableStateOf<List<JourneyUiModel>>(emptyList())
        private set

    var sortType = mutableStateOf(SortType.DATE)
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun loadJourneys() {
        viewModelScope.launch {
            isLoading.value = true

            val data = repository.getCompletedJourneys()
            journeys.value = sort(data, sortType.value)

            isLoading.value = false
        }
    }

    fun changeSort(type: SortType) {
        sortType.value = type
        journeys.value = sort(journeys.value, type)
    }

    private fun sort(
        list: List<JourneyUiModel>,
        type: SortType
    ): List<JourneyUiModel> {
        return when (type) {
            SortType.DATE -> list.sortedByDescending { it.completedAt }
            SortType.KM -> list.sortedByDescending { it.km }
            SortType.NAME -> list.sortedBy { it.destinationName }
        }
    }
}