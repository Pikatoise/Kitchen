package com.example.kitchen.supabase.repositories

import com.example.kitchen.models.Like
import com.example.kitchen.supabase.interfaces.LikeRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : LikeRepository {
    override suspend fun getDishLikes(dishId: Int): List<Like> {
        return withContext(Dispatchers.IO){
            postgrest.from("Likes").select{
                filter {
                    eq("DishId", dishId)
                }
            }.decodeList<Like>()
        }
    }
}