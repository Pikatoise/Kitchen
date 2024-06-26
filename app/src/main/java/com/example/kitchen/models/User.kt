package com.example.kitchen.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("Id")
    val id: Int,
    @SerialName("Login")
    val login: String,
    @SerialName("Password")
    val password: String
)
