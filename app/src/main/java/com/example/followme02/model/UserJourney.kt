package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserJourney(
    val id: Int,

    @SerialName("user_id")
    val userId: Int,

    @SerialName("destination_id")
    val destinationId: Int,

    @SerialName("completed_at")
    val completedAt: String
)

data class JourneyUiModel(
    val destinationId: Int,
    val destinationName: String,
    val km: Double,
    val completedAt: String,
    val factText: String?,
    val imageUrl: String?
)