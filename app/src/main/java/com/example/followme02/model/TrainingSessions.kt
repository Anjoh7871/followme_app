package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingSessions(
    @SerialName("session_id")
    val sessionId: Int,

    @SerialName("user_id")
    val userId: Int?,

    @SerialName("type_id")
    val typeId: Int?,

    @SerialName("distance_km")
    val distanceKm: Double,

    @SerialName("duration_minutes")
    val durationMinutes: Int?,

    @SerialName("training_date")
    val trainingDate: String?
)