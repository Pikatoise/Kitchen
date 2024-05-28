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
import com.example.kitchen.databinding.ActivityUserFavoritesBinding
import com.example.kitchen.databinding.ActivityUserLikesBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.Favorite
import com.example.kitchen.models.Like
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.FavoriteRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.FavoriteRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class UserLikesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLikesBinding
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId = -1
    private var isStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserLikesBinding.inflate(layoutInflater)

        val provider = SupabaseModule.provideSupabaseDatabase()
        dishRepository = DishRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)

        preferencesRepository = PreferencesRepository(this)

        profileId = preferencesRepository.getProfileId()

        if (profileId <= 0)
            finish()

        loadLikedDishes{dishes, likes ->
            if (dishes.isEmpty()){
                binding.tvLikesLoading.visibility = View.VISIBLE
            }
            else{
                binding.tvLikesLoading.visibility = View.INVISIBLE

                binding.rvLikes.adapter = DishesAdapter(dishes, likes) {
                    val intent = Intent(this, DishActivity::class.java)

                    intent.putExtra("dishId", it)

                    startActivity(intent)
                }
            }
        }

        binding.ivLikesExit.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    @Override
    override fun onResume() {
        super.onResume()

        if (!isStart){
            binding.rvLikes.adapter = null

            loadLikedDishes{dishes, likes ->
                if (dishes.isEmpty())
                    binding.tvLikesLoading.visibility = View.VISIBLE
                else{
                    binding.tvLikesLoading.visibility = View.INVISIBLE

                    binding.rvLikes.adapter = DishesAdapter(dishes, likes) {
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

    private fun loadLikedDishes(callback: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var allDishes: List<Dish> = arrayListOf()
        var likedDishes: MutableList<Dish> = mutableListOf()
        var profileLikes: List<Like> = arrayListOf()
        var likesCounts: IntArray

        lifecycleScope.launch {
            allDishes = dishRepository.getAllDishes()
            profileLikes = likeRepository.getProfileLikes(profileId)
        }.invokeOnCompletion {
            allDishes.forEach {
                if (profileLikes.any { x -> x.dishId == it.id })
                    likedDishes.add(it)
            }

            likesCounts = IntArray(likedDishes.count())

            if (likedDishes.isEmpty()){
                callback(likedDishes,likesCounts.toList())
                return@invokeOnCompletion
            }

            for(i in 0 until likedDishes.count()){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(likedDishes[i].id).count()
                }.invokeOnCompletion {
                    if (i == likedDishes.count() - 1)
                        callback(likedDishes, likesCounts.toList())
                }
            }
        }
    }
}