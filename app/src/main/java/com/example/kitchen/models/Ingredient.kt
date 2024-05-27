package com.example.kitchen.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("Description")
    val description: String,
    @SerialName("DishId")
    val dishId: Int
)
