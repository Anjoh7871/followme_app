package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AchievementsType(
    @SerialName("type_id")
    val typeId: Long,

    @SerialName("type_name")
    val typeName: String
)