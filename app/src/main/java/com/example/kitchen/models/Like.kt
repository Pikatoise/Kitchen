package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Like(
    val id: Int,
    val profileId: Int,
    val dishId: Int
)
