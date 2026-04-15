package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRoles(
    val userId: Int,
    val roleId: Int
)