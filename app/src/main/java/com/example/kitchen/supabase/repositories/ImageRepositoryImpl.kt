package com.example.kitchen.supabase.repositories

import com.example.kitchen.supabase.interfaces.ImageRepository
import io.github.jan.supabase.storage.Bucket
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val bucket: Storage
): ImageRepository {
    override suspend fun loadAvatar(name: String, image: ByteArray) {
        withContext(Dispatchers.IO) {
            bucket.from("kitchen_user_avatars").upload(
                path = name,
                data = image,
                upsert = true
            )
        }
    }

    override suspend fun deleteAvatar(name: String){
        withContext(Dispatchers.IO) {
            bucket.from("kitchen_user_avatars").delete(name)
        }
    }
}