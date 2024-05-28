package com.example.kitchen.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityUserFavoritesBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.Favorite
import com.example.kitchen.sqlite.Preferences
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.FavoriteRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.FavoriteRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class UserFavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserFavoritesBinding
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId = -1
    private var isStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserFavoritesBinding.inflate(layoutInflater)

        val provider = SupabaseModule.provideSupabaseDatabase()
        dishRepository = DishRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)
        favoriteRepository = FavoriteRepositoryImpl(provider)

        preferencesRepository = PreferencesRepository(this)

        profileId = preferencesRepository.getProfileId()

        if (profileId <= 0)
            finish()

        loadFavoriteDishes{dishes, likes ->
            if (dishes.isEmpty()){
                binding.tvFavoritesLoading.visibility = View.VISIBLE
            }
            else{
                binding.tvFavoritesLoading.visibility = View.INVISIBLE

                binding.rvFavorites.adapter = DishesAdapter(dishes, likes) {
                    val intent = Intent(this, DishActivity::class.java)

                    intent.putExtra("dishId", it)

                    startActivity(intent)
                }
            }
        }

        binding.ivFavoritesExit.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    @Override
    override fun onResume() {
        super.onResume()

        if (!isStart){
            binding.rvFavorites.adapter = null

            loadFavoriteDishes{dishes, likes ->
                if (dishes.isEmpty())
                    binding.tvFavoritesLoading.visibility = View.VISIBLE
                else{
                    binding.tvFavoritesLoading.visibility = View.INVISIBLE

                    binding.rvFavorites.adapter = DishesAdapter(dishes, likes) {
                        val intent = Intent(this, DishActivity::class.java)

                        intent.putExtra("dishId", it)

                        startActivity(intent)
                    }
                }
            }
        }
        else
            isStart = false
    }

    private fun loadFavoriteDishes(callback: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var allDishes: List<Dish> = arrayListOf()
        var favoriteDishes: MutableList<Dish> = mutableListOf()
        var profileFavorites: List<Favorite> = arrayListOf()
        var likesCounts: IntArray

        lifecycleScope.launch {
            allDishes = dishRepository.getAllDishes()
            profileFavorites = favoriteRepository.getProfileFavorites(profileId)
        }.invokeOnCompletion {
            allDishes.forEach {
                if (profileFavorites.any { x -> x.dishId == it.id })
                    favoriteDishes.add(it)
            }

            likesCounts = IntArray(favoriteDishes.count())

            if (favoriteDishes.isEmpty()){
                callback(favoriteDishes,likesCounts.toList())
                return@invokeOnCompletion
            }

            for(i in 0 until favoriteDishes.count()){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(favoriteDishes[i].id).count()
                }.invokeOnCompletion {
                    if (i == favoriteDishes.count() - 1)
                        callback(favoriteDishes, likesCounts.toList())
                }
            }
        }
    }
}