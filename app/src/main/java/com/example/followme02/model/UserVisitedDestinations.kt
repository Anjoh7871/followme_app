package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserVisitedDestinations(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("destination_id")
    val destinationId: Int,

    @SerialName("visited_at")
    val visitedAt: String? = null
)