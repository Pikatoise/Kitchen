package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Like

interface LikeRepository{
    suspend fun getDishLikes(dishId: Int): List<Like>

    suspend fun getAllLikes(): List<Like>

    suspend fun getProfileLikes(profileId: Int): List<Like>

    suspend fun setDishLike(profileId: Int, dishId: Int): Boolean

    suspend fun removeDishLike(profileId: Int, dishId: Int)
}