package com.example.kitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Int,
    val name: String,
    val avatar: String,
    val userId: Int
)
