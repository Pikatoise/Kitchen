package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: Int,
    val profileId: Int,
    val dishId: Int
)
