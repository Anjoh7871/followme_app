package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val userId: Int,
    val username: String,
    val email: String,
    val passwordHash: String?,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?,
    val currentLevel: Int,
    val totalPoints: Int,
    val totalAccumulatedKm: Double,
    val isActive: Boolean,
    val createdAt: String?,
    val selectedDestinationId: Int? = null
)