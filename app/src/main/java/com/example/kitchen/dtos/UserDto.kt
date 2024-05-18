package com.example.kitchen.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("Login")
    val login: String,
    @SerialName("Password")
    val password: String
)
