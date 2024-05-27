package com.example.kitchen.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeDto(
    @SerialName("ProfileId")
    val profileId: Int,
    @SerialName("DishId")
    val dishId: Int
)
