package com.example.kitchen.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityAllDishesBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.Like
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class AllDishesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDishesBinding
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAllDishesBinding.inflate(layoutInflater)

        dishRepository = DishRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        likeRepository = LikeRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        loadAllDishes{dishes, likes ->
            if (dishes.count() == 0)
                binding.tvAllDishesLoading.visibility = View.VISIBLE
            else
                binding.tvAllDishesLoading.visibility = View.INVISIBLE

            binding.rvAllDishes.adapter = DishesAdapter(dishes, likes) {
                val intent = Intent(this, DishActivity::class.java)

                intent.putExtra("dishId", it)

                startActivity(intent)
            }
        }

        binding.ivAllDishesExit.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    private fun loadAllDishes(callback: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var allDishes: List<Dish> = arrayListOf()
        var likesCounts: MutableList<Int> = mutableListOf()

        lifecycleScope.launch {
            allDishes = dishRepository.getAllDishes()
        }.invokeOnCompletion {
            allDishes.forEach {
                var likes: List<Like> = listOf()

                lifecycleScope.launch {
                    likes = likeRepository.getDishLikes(it.id)
                }.invokeOnCompletion {
                    likesCounts.add(likes.count())

                    if (likesCounts.count() == allDishes.count())
                        callback(allDishes,likesCounts)
                }
            }
        }
    }
}