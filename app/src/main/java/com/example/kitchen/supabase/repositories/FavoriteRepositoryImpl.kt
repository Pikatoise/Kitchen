package com.example.kitchen.supabase.repositories

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
}