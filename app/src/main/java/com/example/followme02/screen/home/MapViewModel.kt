package com.example.followme02.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.MapRepository
import com.example.followme02.model.Destinations
import com.example.followme02.model.UserVisitedDestinations
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class MapViewModel : ViewModel() {

    private val mapRepository = MapRepository()

    var mapPoints by mutableStateOf<List<MapPointUI>>(emptyList())
        private set

    // Track which destination IDs have been manually hidden by the user
    var hiddenDestinationNames by mutableStateOf<Set<String>>(emptySet())
        private set

    fun loadMapData(
        destinations: List<Destinations>,
        visitedIds: Set<Int>,
        selectedDestinationId: Int? = null // CHANGED: added to identify the active journey destination (red pin)
    ) {
        viewModelScope.launch {
            mapPoints = buildMapPoints(destinations, visitedIds, selectedDestinationId)
        }
    }

    // New function — called from UI when user taps "Remove marker"
    fun hideMarker(name: String) {
        hiddenDestinationNames = hiddenDestinationNames + name
    }

    // New function — resets all hidden markers (optional, exposed for UI)
    fun resetHiddenMarkers() {
        hiddenDestinationNames = emptySet()
    }

    private suspend fun buildMapPoints(
        destinations: List<Destinations>,
        visitedIds: Set<Int>,
        selectedDestinationId: Int? = null // CHANGED: the active journey destination (red pin)
    ): List<MapPointUI> {

        val visited = destinations
            .filter { it.destinationId in visitedIds }
            .sortedByDescending { it.kmThreshold } // latest visited = highest threshold reached

        val unvisited = destinations
            .filter { it.destinationId !in visitedIds }
            .sortedBy { it.kmThreshold } // next unvisited = lowest threshold not yet reached

        val relevantDestinations: List<Destinations> = if (unvisited.isEmpty()) {
            // All destinations visited — show 4 latest visited
            visited.take(4)
        } else {
            // CHANGED: Use selectedDestinationId as the unvisited (red) pin so it matches
            // the destination shown in "Your Virtual Journey". Falls back to next by kmThreshold
            // if selectedDestinationId is null or not found among unvisited.
            val activeUnvisited = unvisited.firstOrNull { it.destinationId == selectedDestinationId }
                ?: unvisited.first()
            visited.take(3) + activeUnvisited
        }

        return relevantDestinations.mapNotNull { dest ->
            val geo = mapRepository.geocodeCity(dest.name)

            Log.d("GEOCODE", "City: ${dest.name}, Result: $geo")

            geo ?: return@mapNotNull null

            MapPointUI(
                name = dest.name,
                location = geo,
                threshold = dest.kmThreshold,
                isVisited = dest.destinationId in visitedIds
            )
        }
    }
}