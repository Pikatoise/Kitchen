package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val Id: Int,
    val Login: String,
    val Password: String
)
