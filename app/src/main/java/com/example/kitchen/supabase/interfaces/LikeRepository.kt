package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Like

interface LikeRepository{
    suspend fun getDishLikes(dishId: Int): List<Like>
}