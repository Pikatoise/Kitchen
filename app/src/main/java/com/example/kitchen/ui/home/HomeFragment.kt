package com.example.kitchen.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.activities.AllDishesActivity
import com.example.kitchen.activities.DishActivity
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
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var dishes: List<Dish>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dishRepository = DishRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        likeRepository = LikeRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        loadDishes().invokeOnCompletion {
            if (dishes.isEmpty()) {
                binding.tvHomeRandomLoading.visibility = VISIBLE
                binding.tvHomeNewLoading.visibility = VISIBLE

                return@invokeOnCompletion
            }
            else {
                binding.tvHomeNewLoading.visibility = INVISIBLE
                binding.tvHomeRandomLoading.visibility = INVISIBLE
            }

            val clickCallBack: (dishId: Int) -> Unit = {
                val intent = Intent(this.requireContext(), DishActivity::class.java)

                intent.putExtra("dishId", it)

                startActivity(intent)
            }

            loadRandomDishes { dishes, likes ->
                binding.rvHomeRandomDishes.adapter = DishesAdapter(dishes, likes, clickCallBack)

                binding.rvHomeRandomDishes.addItemDecoration(CirclePagerIndicatorDecoration())

                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            loadNewDish { dishes, likes ->
                binding.rvHomeNewDishes.adapter = DishesAdapter(dishes, likes, clickCallBack)

                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        binding.tvHomeAllDishes.setOnClickListener {
            val i = Intent(this.requireContext(), AllDishesActivity::class.java)

            startActivity(i)
        }

        return binding.root
    }

    private fun loadDishes(): Job{
        return lifecycleScope.launch {
            dishes = dishRepository.getAllDishes()
        }
    }

    private fun loadRandomDishes(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit) {
        var selectedDishes: MutableList<Dish> = mutableListOf()
        var selectedDishesLikesCount: MutableList<Int> = mutableListOf()

        if (dishes.count() <= 3) {
            var dishLikes: List<Like> = listOf()

            dishes.forEach {
                selectedDishes.add(it)

                lifecycleScope.launch {
                    dishLikes = likeRepository.getDishLikes(it.id)
                }.invokeOnCompletion {
                    selectedDishesLikesCount.add(dishLikes.count())

                    if (selectedDishesLikesCount.count() == dishes.count())
                        callBack(selectedDishes, selectedDishesLikesCount)
                }
            }

        }
        else{
            val dishesForPick = dishes.toMutableList()

            for(i in 0..2){
                val selectedDishIndex = (0..dishesForPick.count() - 1).random()

                selectedDishes.add(dishesForPick[selectedDishIndex])

                dishesForPick.removeAt(selectedDishIndex)
            }

            var likes: List<Like> = listOf()
            selectedDishes.forEach {
                lifecycleScope.launch {
                    likes = likeRepository.getDishLikes(it.id)
                }.invokeOnCompletion {
                    selectedDishesLikesCount.add(likes.count())

                    if (selectedDishesLikesCount.count() == selectedDishes.count())
                        callBack(selectedDishes,selectedDishesLikesCount)
                }
            }
        }
    }

    private fun loadNewDish(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var dish: Dish = dishes.last()
        var likesCounts: List<Like> = listOf()

        lifecycleScope.launch {
            likesCounts = likeRepository.getDishLikes(dish.id)
        }.invokeOnCompletion {
            callBack(List<Dish>(1) { dish }, List<Int>(1)  { likesCounts.count() })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}