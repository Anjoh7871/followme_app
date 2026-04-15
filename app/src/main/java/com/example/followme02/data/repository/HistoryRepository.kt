package com.example.historylogikkogimplementering.history

class HistoryRepository {

    /**
     * Summer total km per aktivitet basert på historikk lista
     */
    fun calculateDistanceTotals(
        events: List<HistoryEvent>
    ): Map<DistanceActivity, Double> {

        // Startverdier (alt 0.0)
        val totals = mutableMapOf(
            DistanceActivity.SKI to 0.0,
            DistanceActivity.CYCLING to 0.0,
            DistanceActivity.JOGGING to 0.0,
            DistanceActivity.SWIMMING to 0.0
        )

        for (event in events) {
            // Vi teller kun DISTANCE events
            if (event.type != HistoryType.DISTANCE) continue

            // Må ha aktivitet og km for å telle
            val activity = event.distanceActivity ?: continue
            val km = event.distanceKm ?: continue

            // Legg til km for aktiviteten
            totals[activity] = totals.getValue(activity) + km
        }

        return totals
    }
}
