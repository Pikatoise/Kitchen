package com.example.kitchen.supabase.interfaces

import com.example.kitchen.dtos.DishDto
import com.example.kitchen.models.Dish

interface DishRepository {
    suspend fun getAllDishes(): List<Dish>

    suspend fun getLastDish(): Dish?

    suspend fun getDish(id: Int): Dish?

    suspend fun getProfileDishes(profileId: Int): List<Dish>

    suspend fun addDish(dto: DishDto): Dish?

    suspend fun updateDishImage(dishId: Int, path: String)
}