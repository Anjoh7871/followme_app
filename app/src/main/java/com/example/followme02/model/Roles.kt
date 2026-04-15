package com.example.followme02.model

import kotlinx.serialization.Serializable

@Serializable
data class Roles(
    val roleId: Int,
    val roleName: String
)

