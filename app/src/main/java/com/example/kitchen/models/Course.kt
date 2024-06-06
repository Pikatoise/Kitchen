package com.example.kitchen.models

data class Course (
    val name: String,
    val imageId: Int,
    val clickCallBack: () -> Unit
)