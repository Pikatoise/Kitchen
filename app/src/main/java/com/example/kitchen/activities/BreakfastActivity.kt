package com.example.kitchen.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityBreakfastBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class BreakfastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBreakfastBinding

    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBreakfastBinding.inflate(layoutInflater)

        val provider = SupabaseModule.provideSupabaseDatabase()

        dishRepository = DishRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)

        binding.ivBreakfastExit.setOnClickListener {
            finish()
        }

        loadDishes {dishes, likes ->
            binding.rvBreakfastDishes.adapter = DishesAdapter(dishes, likes) {
                val intent = Intent(this, DishActivity::class.java)

                intent.putExtra("dishId", it)

                startActivity(intent)
            }
        }

        setContentView(binding.root)
    }

    private fun loadDishes(callback: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var dishes: List<Dish> = arrayListOf()
        var likesCounts: IntArray

        lifecycleScope.launch {
            dishes = dishRepository.getDishesByCategory(5)
        }.invokeOnCompletion {
            likesCounts = IntArray(dishes.count())

            if (dishes.isEmpty()){
                binding.tvBreakfastDishesLoading.text = "Пусто"
                return@invokeOnCompletion
            }

            for(i in 0 until dishes.count()){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(dishes[i].id).count()
                }.invokeOnCompletion {
                    if (i == dishes.count() - 1)
                        callback(dishes, likesCounts.toList())
                }
            }
        }
    }
}