package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Dish

interface DishRepository {
    suspend fun getAllDishes(): List<Dish>

    suspend fun getLastDish(): Dish?

    suspend fun getDish(id: Int): Dish?
}