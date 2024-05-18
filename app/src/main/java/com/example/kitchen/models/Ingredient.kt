package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Int,
    val name: String,
    val description: String,
    val dishId: Int
)
