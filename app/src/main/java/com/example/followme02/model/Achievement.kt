package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    @SerialName("achievement_id")
    val achievementId: Int,

    val title: String,

    val description: String?,

    @SerialName("points_reward")
    val pointsReward: Int? = null,

    @SerialName("type_id")
    val typeId: Long,

    @SerialName("req_unit_id")
    val reqUnitId: Long,

    @SerialName("req_value")
    val reqValue: Long?,

    @SerialName("icon_url")
    val iconUrl: String? = null
)