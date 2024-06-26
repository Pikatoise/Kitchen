package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.LikeDto
import com.example.kitchen.dtos.UserDto
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

    override suspend fun getAllLikes(): List<Like> {
        return withContext(Dispatchers.IO){
            postgrest.from("Likes").select{}.decodeList<Like>()
        }
    }

    override suspend fun getProfileLikes(profileId: Int): List<Like> {
        return withContext(Dispatchers.IO){
            postgrest.from("Likes").select{
                filter {
                    eq("ProfileId", profileId)
                }
            }.decodeList<Like>()
        }
    }

    override suspend fun setDishLike(profileId: Int, dishId: Int): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val newLikeData = LikeDto(profileId, dishId)

                postgrest.from("Likes").insert(newLikeData)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun removeDishLike(profileId: Int, dishId: Int) {
        return withContext(Dispatchers.IO) {
            postgrest.from("Likes").delete {
                filter {
                    eq("ProfileId", profileId)
                    eq("DishId", dishId)
                }
            }
        }
    }
}