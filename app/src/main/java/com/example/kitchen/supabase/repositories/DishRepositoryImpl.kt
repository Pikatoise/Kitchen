package com.example.kitchen.supabase.repositories

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
}