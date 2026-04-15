package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AchievementReqUnit(
    @SerialName("req_unit_id")
    val reqUnitId: Long,

    val unit: String
)