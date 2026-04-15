package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.DestinationRepository
import com.example.followme02.data.repository.JourneyRepository
import com.example.followme02.model.Destinations
import kotlinx.coroutines.launch

class DestinationViewModel : ViewModel() {

    private val repository = DestinationRepository()
    private val journeyRepository = JourneyRepository()

    var allDestinations = mutableStateOf<List<Destinations>>(emptyList())
        private set

    var filteredDestinations = mutableStateOf<List<Destinations>>(emptyList())
        private set

    var selectedDestination = mutableStateOf<Destinations?>(null)
        private set

    var journeyStartKm = mutableStateOf(0.0)
        private set

    var currentJourneyKm = mutableStateOf(0.0)
        private set

    var searchText = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    var isCompletingJourney = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var recentlyCompletedDestination = mutableStateOf<Destinations?>(null)
        private set

    fun loadDestinations() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val destinations = repository.getAllDestinations()
                    .filter { it.name != "Narvik" }

                allDestinations.value = destinations
                filteredDestinations.value = destinations

                val currentJourney = repository.getCurrentJourneySelection()
                val selectedId = currentJourney?.selectedDestinationId
                val startKm = currentJourney?.journeyStartKm ?: 0.0

                selectedDestination.value = destinations.find { it.destinationId == selectedId }
                journeyStartKm.value = startKm
            } catch (e: Exception) {
                errorMessage.value = "Failed to load destinations"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText

        filteredDestinations.value = if (newText.isBlank()) {
            allDestinations.value
        } else {
            allDestinations.value.filter {
                it.name.startsWith(newText, ignoreCase = true)
            }
        }
    }

    fun selectDestination(
        destination: Destinations,
        currentTotalKm: Double
    ) {
        viewModelScope.launch {
            val success = repository.saveSelectedDestination(
                destinationId = destination.destinationId,
                currentTotalKm = currentTotalKm
            )

            if (success) {
                selectedDestination.value = destination
                journeyStartKm.value = currentTotalKm
                currentJourneyKm.value = 0.0
            } else {
                errorMessage.value = "Failed to save selected destination"
            }
        }
    }

    fun syncCurrentJourney(currentTotalKm: Double) {
        val destination = selectedDestination.value

        if (destination == null) {
            currentJourneyKm.value = 0.0
            return
        }

        val progressKm = (currentTotalKm - journeyStartKm.value).coerceAtLeast(0.0)
        currentJourneyKm.value = progressKm

        if (progressKm >= destination.kmThreshold && !isCompletingJourney.value) {
            isCompletingJourney.value = true

            viewModelScope.launch {
                val completedDestination = destination
                val insertSuccess = journeyRepository.insertCompletedJourney(completedDestination.destinationId)

                if (insertSuccess) {
                    recentlyCompletedDestination.value = completedDestination

                    repository.clearCurrentJourney()

                    selectedDestination.value = null
                    journeyStartKm.value = 0.0
                    currentJourneyKm.value = 0.0
                    searchText.value = ""
                } else {
                    errorMessage.value = "Failed to save completed journey"
                }

                isCompletingJourney.value = false
            }
        }
    }

    fun clearRecentlyCompletedDestination() {
        recentlyCompletedDestination.value = null
    }
}