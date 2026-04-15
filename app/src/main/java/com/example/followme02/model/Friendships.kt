package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class Friendships(
    val userId1: Int,
    val userId2: Int,
    val statusId: Int?,
    val actionUserId: Int?,
    val createdAt: String?
)