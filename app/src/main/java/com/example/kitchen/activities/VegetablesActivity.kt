package com.example.kitchen.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityVegetablesBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class VegetablesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVegetablesBinding

    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVegetablesBinding.inflate(layoutInflater)

        val provider = SupabaseModule.provideSupabaseDatabase()

        dishRepository = DishRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)

        binding.ivVegetablesExit.setOnClickListener {
            finish()
        }

        loadDishes {dishes, likes ->
            binding.rvVegetablesDishes.adapter = DishesAdapter(dishes, likes) {
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
            dishes = dishRepository.getDishesByCategory(4)
        }.invokeOnCompletion {
            likesCounts = IntArray(dishes.count())

            if (dishes.isEmpty()){
                binding.tvVegetablesDishesLoading.text = "Пусто"
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