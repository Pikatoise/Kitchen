package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.FavoriteDto
import com.example.kitchen.dtos.LikeDto
import com.example.kitchen.models.Favorite
import com.example.kitchen.supabase.interfaces.FavoriteRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : FavoriteRepository {
    override suspend fun getProfileFavorites(profileId: Int): List<Favorite> {
        return withContext(Dispatchers.IO){
            postgrest.from("Favorites").select{
                filter {
                    eq("ProfileId", profileId)
                }
            }.decodeList<Favorite>()
        }
    }

    override suspend fun setDishFavorite(profileId: Int, dishId: Int): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val newFavoriteData = FavoriteDto(profileId, dishId)

                postgrest.from("Favorites").insert(newFavoriteData)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun removeDishFavorite(profileId: Int, dishId: Int) {
        return withContext(Dispatchers.IO) {
            postgrest.from("Favorites").delete {
                filter {
                    eq("ProfileId", profileId)
                    eq("DishId", dishId)
                }
            }
        }
    }
}