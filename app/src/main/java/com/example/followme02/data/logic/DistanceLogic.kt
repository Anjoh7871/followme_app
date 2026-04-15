package com.example.followme

import com.example.followme02.model.WorkoutDraft
import kotlinx.serialization.Serializable

// ---------------- MODELS ----------------


/**
 * Matcher Supabase-tabellen: training_sessions
 */
@Serializable
data class TrainingSessionRow(
    val session_id: String? = null,
    val user_id: String,
    val type_id: Int,
    val distance_km: Double,
    val duration_minutes: Int,
    val training_date: String
)

// ---------------- DISTANCE LOGIC ----------------

sealed class DistanceResult {
    data class Success(val updatedDraft: WorkoutDraft) : DistanceResult()
    data class Error(val reason: DistanceError) : DistanceResult()
}

enum class DistanceError {
    REQUIRED,
    INVALID_NUMBER,
    MUST_BE_GREATER_THAN_ZERO
}

fun setDistanceKm(draft: WorkoutDraft, input: String): DistanceResult {
    val normalized = input.replace(",", ".").trim()

    if (normalized.isBlank()) {
        return DistanceResult.Error(DistanceError.REQUIRED)
    }

    val distanceKm = normalized.toDoubleOrNull()
        ?: return DistanceResult.Error(DistanceError.INVALID_NUMBER)

    if (distanceKm <= 0.0) {
        return DistanceResult.Error(DistanceError.MUST_BE_GREATER_THAN_ZERO)
    }

    return DistanceResult.Success(
        updatedDraft = draft.copy(distanceKm = distanceKm)
    )
}