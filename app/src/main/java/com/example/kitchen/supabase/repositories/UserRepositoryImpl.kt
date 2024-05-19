package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.UserDto
import com.example.kitchen.models.User
import com.example.kitchen.supabase.interfaces.UserRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : UserRepository {
    override suspend fun createUser(user: User): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val user = UserDto(user.login,user.password)

                postgrest.from("Users").insert(user)

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

    override suspend fun updateUser(id: Int, name: String, password: String) {

    }
}