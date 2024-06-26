package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.ProfileDto
import com.example.kitchen.models.Profile
import com.example.kitchen.supabase.interfaces.ProfileRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : ProfileRepository{
    override suspend fun getUserProfile(userId: Int): Profile? {
        return withContext(Dispatchers.IO){
            postgrest.from("Profiles").select{
                filter {
                    eq("UserId", userId)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun getProfile(id: Int): Profile? {
        return withContext(Dispatchers.IO){
            postgrest.from("Profiles").select{
                filter {
                    eq("Id", id)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun createUserProfile(userId: Int): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val newProfileData = ProfileDto("name_${userId}","",userId)

                postgrest.from("Profiles").insert(newProfileData)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun updateProfileName(profileId: Int, newName: String) {
        withContext(Dispatchers.IO){
            postgrest.from("Profiles").update(
                {
                    set("Name", newName)
                }
            ) {
                filter {
                    eq("Id", profileId)
                }
            }
        }
    }
}