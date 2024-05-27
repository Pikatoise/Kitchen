package com.example.kitchen.supabase.interfaces

import com.example.kitchen.models.Ingredient

interface IngredientRepository {
    suspend fun getDishIngredients(dishId: Int): List<Ingredient>
}