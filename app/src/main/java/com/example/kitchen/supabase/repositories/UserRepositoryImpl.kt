package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.UserDto
import com.example.kitchen.models.User
import com.example.kitchen.supabase.interfaces.UserRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : UserRepository {
    override suspend fun createUser(user: User): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val newUserData = UserDto(user.login,user.password)

                postgrest.from("Users").insert(newUserData)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun getUser(id: Int): User? {
        return withContext(Dispatchers.IO){
            postgrest.from("Users").select{
                filter {
                    eq("Id", id)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun getUserByLogin(login: String): User? {
        return withContext(Dispatchers.IO){
            postgrest.from("Users").select{
                filter {
                    eq("Login", login)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun updateUserPassword(userId: Int, newPassword: String) {
        withContext(Dispatchers.IO){
            postgrest.from("Users").update(
                {
                    set("Password", newPassword)
                }
            ) {
                filter {
                    eq("Id", userId)
                }
            }
        }
    }
}