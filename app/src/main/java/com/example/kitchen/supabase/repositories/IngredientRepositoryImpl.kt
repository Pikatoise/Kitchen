package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.IngredientDto
import com.example.kitchen.dtos.ProfileDto
import com.example.kitchen.models.Ingredient
import com.example.kitchen.models.Like
import com.example.kitchen.supabase.interfaces.IngredientRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
): IngredientRepository {
    override suspend fun getDishIngredients(dishId: Int): List<Ingredient> {
        return withContext(Dispatchers.IO){
            postgrest.from("Ingredients").select{
                filter {
                    eq("DishId", dishId)
                }
            }.decodeList<Ingredient>()
        }
    }

    override suspend fun addIngredient(dto: IngredientDto) {
        withContext(Dispatchers.IO) {
            postgrest.from("Ingredients").insert(dto)
        }
    }
}