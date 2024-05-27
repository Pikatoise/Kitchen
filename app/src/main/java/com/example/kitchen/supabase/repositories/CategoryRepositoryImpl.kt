package com.example.kitchen.supabase.repositories

import com.example.kitchen.models.DishCategory
import com.example.kitchen.supabase.interfaces.CategoryRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
): CategoryRepository {
    override suspend fun getCategory(categoryId: Int): DishCategory? {
        return withContext(Dispatchers.IO){
            postgrest.from("DishCategories").select{
                filter {
                    eq("Id", categoryId)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun getAllCategories(): List<DishCategory> {
        return withContext(Dispatchers.IO){
            postgrest.from("DishCategories").select()
                .decodeList<DishCategory>()
        }
    }
}