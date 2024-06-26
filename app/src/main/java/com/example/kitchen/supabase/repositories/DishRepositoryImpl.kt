package com.example.kitchen.supabase.repositories

import com.example.kitchen.dtos.DishDto
import com.example.kitchen.models.Dish
import com.example.kitchen.supabase.interfaces.DishRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DishRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : DishRepository {
    override suspend fun getAllDishes(): List<Dish> {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").select()
                .decodeList<Dish>()
        }
    }

    override suspend fun getLastDish(): Dish? {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").select{
                limit(1)
                order(column = "Id", order = Order.DESCENDING)
            }.decodeSingleOrNull()
        }
    }

    override suspend fun getDish(id: Int): Dish? {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").select{
                filter {
                    eq("Id", id)
                }
            }.decodeSingleOrNull()
        }
    }

    override suspend fun getProfileDishes(profileId: Int): List<Dish> {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").select{
                filter {
                    eq("ProfileId", profileId)
                }
                order(column = "Id", order = Order.DESCENDING)
            }.decodeList<Dish>()
        }
    }

    override suspend fun getDishesByCategory(categoryId: Int): List<Dish> {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").select{
                filter {
                    eq("CategoryId", categoryId)
                }
            }.decodeList<Dish>()
        }
    }

    override suspend fun addDish(dto: DishDto): Dish? {
        return withContext(Dispatchers.IO){
            postgrest.from("Dishes").insert(dto) {
                select()
            }.decodeSingleOrNull()
        }
    }

    override suspend fun updateDishImage(dishId: Int, path: String) {
        withContext(Dispatchers.IO){
            postgrest.from("Dishes").update(
                {
                    set("Image", path)
                }
            ) {
                filter {
                    eq("Id", dishId)
                }
            }
        }
    }
}