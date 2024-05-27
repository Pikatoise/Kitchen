package com.example.kitchen.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DishCategory(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String
)
