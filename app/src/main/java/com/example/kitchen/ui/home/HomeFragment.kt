package com.example.kitchen.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.activities.AllDishesActivity
import com.example.kitchen.activities.MainActivity
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.lists.CirclePagerIndicatorDecoration
import com.example.kitchen.models.Dish
import com.example.kitchen.models.Like
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dishRepository = DishRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        likeRepository = LikeRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        binding.rvHomeRandomDishes.addItemDecoration(CirclePagerIndicatorDecoration())

        loadRandomDishes { dishes, likes ->
            if (dishes.count() == 0)
                binding.tvHomeRandomLoading.visibility = VISIBLE
            else
                binding.tvHomeRandomLoading.visibility = INVISIBLE

            binding.rvHomeRandomDishes.adapter = DishesAdapter(dishes, likes)
        }

        loadNewDish { dishes, likes ->
            if (dishes.count() == 0)
                binding.tvHomeNewLoading.visibility = VISIBLE
            else
                binding.tvHomeNewLoading.visibility = INVISIBLE

            binding.rvHomeNewDishes.adapter = DishesAdapter(dishes, likes)
        }

        binding.tvHomeAllDishes.setOnClickListener {
            val i = Intent(this.requireContext(), AllDishesActivity::class.java)

            startActivity(i)
        }

        return binding.root
    }

    private fun loadRandomDishes(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit) {
        var allDishes: MutableList<Dish> = arrayListOf()
        var selectedDishes: MutableList<Dish> = mutableListOf()
        var dishes: MutableList<Dish> = mutableListOf()
        var likesCounts: MutableList<Int> = mutableListOf()

        lifecycleScope.launch {
            allDishes = dishRepository.getAllDishes().toMutableList()
        }.invokeOnCompletion {
            if (allDishes.isEmpty())
                return@invokeOnCompletion

            if (allDishes.count() <= 3) {
                var likes: List<Like> = listOf()

                allDishes.forEach {
                    dishes.add(it)

                    lifecycleScope.launch {
                        likes = likeRepository.getDishLikes(it.id)
                    }.invokeOnCompletion {
                        likesCounts.add(likes.count())

                        if (likesCounts.count() == dishes.count())
                            callBack(dishes,likesCounts)
                    }
                }

            }
            else{
                for(i in 0..2){
                    val selectedDishIndex = (0..allDishes.count() - 1).random()

                    selectedDishes.add(allDishes[selectedDishIndex])

                    allDishes.removeAt(selectedDishIndex)
                }

                var likes: List<Like> = listOf()
                selectedDishes.forEach {
                    dishes.add(it)

                    lifecycleScope.launch {
                        likes = likeRepository.getDishLikes(it.id)
                    }.invokeOnCompletion {
                        likesCounts.add(likes.count())

                        if (likesCounts.count() == dishes.count())
                            callBack(dishes,likesCounts)
                    }
                }
            }
        }
    }

    private fun loadNewDish(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var dish: Dish? = null
        var likesCounts: List<Like> = listOf()

        lifecycleScope.launch {
            dish = dishRepository.getLastDish()
        }.invokeOnCompletion {
            lifecycleScope.launch {
                likesCounts = likeRepository.getDishLikes(dish!!.id)
            }.invokeOnCompletion {
                callBack(List<Dish>(1) { dish!! }, List<Int>(1)  {likesCounts.count() })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}