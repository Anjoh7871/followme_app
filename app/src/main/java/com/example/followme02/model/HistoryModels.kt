package com.example.historylogikkogimplementering.history

import java.time.Instant
import java.util.UUID

/**
 * Enums under er en del av API kontrakten.
 *
 * Viktig:
 * Backend bør sende samme tekst som enum-navnene ("DISTANCE" etc).
 */
enum class HistoryType {
    /** Distanse-registrering (ski/sykkel/jogging/svømming) */
    DISTANCE,

    /** Vanlig treningsøkt (styrke, intervaller, osv.) */
    WORKOUT,

    /** Achievement/unlock (badge, mål nådd, streak osv.) */
    ACHIEVEMENT,

    /** Personlig rekord (PR) */
    PR
}

/**
 * Aktiviteter som skal summeres i km. Kan legge til flere i framtiden omså
 * Disse brukes kun når HistoryType == DISTANCE.
 */
enum class DistanceActivity {
    SKI,
    CYCLING,
    JOGGING,
    SWIMMING
}

/**
 * En registrering i historikken eller "økt"
 *
 * Dette er domenemodellen .
 * UI senere kan vise en liste sortert etter occurredAt.
 */
data class HistoryEvent(
    val id: UUID,                    // unik ID fra database
    val type: HistoryType,           // event type
    val occurredAt: Instant,         // når eventet skjedde
    val title: String,               // kort tittel for UI
    val description: String? = null, // valgfri detaljer

    // Kun brukt hvis type == DISTANCE:
    val distanceActivity: DistanceActivity? = null,
    val distanceKm: Double? = null
)
