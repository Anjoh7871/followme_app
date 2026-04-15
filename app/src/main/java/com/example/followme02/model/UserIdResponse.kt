package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserIdResponse(
    @SerialName("user_id")
    val userId: Int
)