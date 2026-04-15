package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class TeamMemberships(
    val teamId: Int,
    val userId: Int,
    val joinedAt: String?
)

