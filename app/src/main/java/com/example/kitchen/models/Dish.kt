package com.example.kitchen.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("Recipe")
    val recipe: String,
    @SerialName("Image")
    val image: String,
    @SerialName("CookingTime")
    val cookingTime: Int,
    @SerialName("PortionCount")
    val portionCount: Int,
    @SerialName("CategoryId")
    val categoryId: Int,
    @SerialName("ProfileId")
    val profileId: Int
)
