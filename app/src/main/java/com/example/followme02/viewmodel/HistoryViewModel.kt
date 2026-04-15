package com.example.historylogikkogimplementering.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI-state som skjermen (senere) kan observere.
 * events: historikklista
 * totalsKm: summerte km per aktivitet
 */
data class HistoryState(
    val events: List<HistoryEvent> = emptyList(),
    val totalsKm: Map<DistanceActivity, Double> = emptyMap()
)

/**
 * ViewModel binder sammen state + repository.
 *
 * UI er ikke på plass, men logikken er klar.
 * Når vi får data fra backend , kall updateHistory(...).
 */
class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    // Internt state (mutable)
    private val _state = MutableStateFlow(HistoryState())

    // Eksternt state (read-only)
    val state: StateFlow<HistoryState> = _state

    /**
     * Oppdaterer state med ny historikk (fra backend).
     */
    fun updateHistory(events: List<HistoryEvent>) {
        val totals = repository.calculateDistanceTotals(events)

        _state.value = HistoryState(
            events = events,
            totalsKm = totals
        )
    }
}
