package com.example.followme02.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingTypes(
    @SerialName("type_id")
    val typeId: Int,

    @SerialName("type_name")
    val typeName: String
)