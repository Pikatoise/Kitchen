package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class DishCategory(
    val id: Int,
    val name: String
)
