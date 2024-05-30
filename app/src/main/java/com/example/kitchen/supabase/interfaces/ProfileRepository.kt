package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Profile

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int): Profile?

    suspend fun getProfile(id: Int): Profile?

    suspend fun createUserProfile(userId: Int): Boolean

    suspend fun updateProfileName(profileId: Int, newName: String)
}