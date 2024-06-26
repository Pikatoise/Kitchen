package com.example.kitchen.supabase.interfaces

interface ImageRepository {
       suspend fun loadAvatar(name: String, image: ByteArray)

       suspend fun deleteAvatar(name: String)

       suspend fun loadDishImage(name: String, image: ByteArray)
}