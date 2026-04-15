package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class Teams(
    val teamId: Int,
    val teamName: String,
    val createdAt: String?
)

