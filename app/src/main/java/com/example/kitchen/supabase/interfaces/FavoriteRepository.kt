package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Favorite

interface FavoriteRepository {
    suspend fun getProfileFavorites(profileId: Int): List<Favorite>
}