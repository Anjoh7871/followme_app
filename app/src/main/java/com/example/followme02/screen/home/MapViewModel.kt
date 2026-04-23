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

    private val mapRepository = MapRepository() // ✅ ADD THIS

    var mapPoints by mutableStateOf<List<MapPointUI>>(emptyList())
        private set

    fun loadMapData(
        destinations: List<Destinations>,
        visitedIds: Set<Int>
    ) {
        viewModelScope.launch {
            mapPoints = buildMapPoints(destinations, visitedIds)
        }
    }

    private suspend fun buildMapPoints(
        destinations: List<Destinations>,
        visitedIds: Set<Int>
    ): List<MapPointUI> {

        return destinations.mapNotNull { dest ->
            val geo = mapRepository.geocodeCity(dest.name) // ✅ FIX HERE

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