package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserJourneySelectionResponse(
    @SerialName("selected_destination_id")
    val selectedDestinationId: Int? = null,

    @SerialName("journey_start_km")
    val journeyStartKm: Double? = null
)