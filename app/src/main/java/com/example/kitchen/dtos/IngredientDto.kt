package com.example.kitchen.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto (
    @SerialName("Name")
    val name: String,
    @SerialName("Description")
    val description: String,
    @SerialName("DishId")
    val dishId: Int
)