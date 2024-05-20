package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Profile

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int): Profile?

    suspend fun createUserProfile(userId: Int): Boolean
}