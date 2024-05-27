package com.example.kitchen.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    @SerialName("Id")
    val id: Int,
    @SerialName("ProfileId")
    val profileId: Int,
    @SerialName("DishId")
    val dishId: Int
)
