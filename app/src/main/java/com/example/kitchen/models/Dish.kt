package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    val id: Int,
    val name: String,
    val recipe: String,
    val image: String,
    val cookingTime: Int,
    val portionCount: Int,
    val categoryId: Int,
    val profileId: Int
)
