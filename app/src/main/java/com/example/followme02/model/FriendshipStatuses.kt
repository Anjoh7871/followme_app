package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendshipStatus(
    val statusId: Int,
    val statusName: String
)

