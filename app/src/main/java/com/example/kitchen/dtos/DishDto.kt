package com.example.kitchen.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DishDto(
    @SerialName("Name")
    val name: String,
    @SerialName("Recipe")
    val recipe: String,
    @SerialName("CookingTime")
    val cookingTime: Int,
    @SerialName("PortionCount")
    val portionCount: Int,
    @SerialName("Image")
    val image: String,
    @SerialName("CategoryId")
    val categoryId: Int,
    @SerialName("ProfileId")
    val profileId: Int
)
