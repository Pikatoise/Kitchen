package com.example.kitchen.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("Name")
    val name: String,
    @SerialName("Avatar")
    val avatar: String,
    @SerialName("UserId")
    val userId: Int
)
