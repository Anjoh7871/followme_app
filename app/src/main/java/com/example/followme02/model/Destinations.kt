package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Destinations(
    @SerialName("destination_id")
    val destinationId: Int,

    val name: String,

    @SerialName("fact_text")
    val factText: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("km_threshold")
    val kmThreshold: Double
)