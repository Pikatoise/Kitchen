package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.User

interface UserRepository {
    suspend fun createUser(user: User): Boolean

    suspend fun getUser(id: Int): User?

    suspend fun getUserByLogin(login: String): User?

    suspend fun updateUserPassword(userId: Int, newPassword: String)
}