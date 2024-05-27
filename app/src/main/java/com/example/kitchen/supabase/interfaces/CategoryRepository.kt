package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.DishCategory


interface CategoryRepository {
    suspend fun getCategory(categoryId: Int): DishCategory?

    suspend fun getAllCategories(): List<DishCategory>
}