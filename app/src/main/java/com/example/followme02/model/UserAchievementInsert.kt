package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAchievementInsert(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("achievement_id")
    val achievementId: Int
)