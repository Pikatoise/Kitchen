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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.activities.AllDishesActivity
import com.example.kitchen.activities.BreakfastActivity
import com.example.kitchen.activities.DishActivity
import com.example.kitchen.activities.MainActivity
import com.example.kitchen.activities.VegetablesActivity
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.lists.CirclePagerIndicatorDecoration
import com.example.kitchen.lists.CoursesAdapter
import com.example.kitchen.models.Course
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

class HomeFragment constructor() : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private var dishes: List<Dish>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dishRepository = DishRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        likeRepository = LikeRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        loadDishes().invokeOnCompletion {
            if (_binding == null)
                return@invokeOnCompletion

            if (dishes!!.isEmpty()) {
                binding.tvHomeRandomLoading.visibility = VISIBLE
                binding.tvHomeNewLoading.visibility = VISIBLE
                binding.tvHomePopularLoading.visibility = VISIBLE

                return@invokeOnCompletion
            }
            else {
                binding.tvHomeNewLoading.visibility = INVISIBLE
                binding.tvHomeRandomLoading.visibility = INVISIBLE
                binding.tvHomePopularLoading.visibility = INVISIBLE
            }

            val clickCallBack: (dishId: Int) -> Unit = {
                val intent = Intent(this.requireContext(), DishActivity::class.java)

                intent.putExtra("dishId", it)

                startActivity(intent)
            }

            loadRandomDishes { dishes, likes ->
                binding.rvHomeRandomDishes.adapter = DishesAdapter(dishes, likes, clickCallBack)

                binding.rvHomeRandomDishes.addItemDecoration(CirclePagerIndicatorDecoration())
            }

            loadNewDish { dishes, likes ->
                binding.rvHomeNewDishes.adapter = DishesAdapter(dishes, likes, clickCallBack)
            }

            loadPopularDish { dishes, likes ->
                binding.rvHomePopularDishes.adapter = DishesAdapter(dishes, likes, clickCallBack)
            }
        }

        binding.tvHomeAllDishes.setOnClickListener {
            val i = Intent(this.requireContext(), AllDishesActivity::class.java)

            startActivity(i)
        }

        loadCourses()

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
        var firstDishLikesCount = 0
        var secondDishLikesCount = 0
        var thirdDishLikesCount = 0

        if (dishes!!.count() <= 3) {
            var dishLikes: List<Like> = listOf()

            dishes!!.forEach {
                selectedDishes.add(it)

                lifecycleScope.launch {
                    dishLikes = likeRepository.getDishLikes(it.id)
                }.invokeOnCompletion {
                    if (_binding == null)
                        return@invokeOnCompletion

                    selectedDishesLikesCount.add(dishLikes.count())

                    if (selectedDishesLikesCount.count() == dishes!!.count())
                        callBack(selectedDishes, selectedDishesLikesCount)
                }
            }

        }
        else{
            val dishesForPick = dishes!!.toMutableList()

            for(i in 0..2){
                val selectedDishIndex = (0..dishesForPick.count() - 1).random()

                selectedDishes.add(dishesForPick[selectedDishIndex])

                dishesForPick.removeAt(selectedDishIndex)
            }

            lifecycleScope.launch {
                firstDishLikesCount = likeRepository.getDishLikes(selectedDishes[0].id).count()
                secondDishLikesCount = likeRepository.getDishLikes(selectedDishes[1].id).count()
                thirdDishLikesCount = likeRepository.getDishLikes(selectedDishes[2].id).count()
            }.invokeOnCompletion {
                if (_binding == null)
                    return@invokeOnCompletion

                selectedDishesLikesCount.add(firstDishLikesCount)
                selectedDishesLikesCount.add(secondDishLikesCount)
                selectedDishesLikesCount.add(thirdDishLikesCount)

                callBack(selectedDishes,selectedDishesLikesCount)
            }
        }
    }

    private fun loadNewDish(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var dish: Dish = dishes!!.maxBy { x -> x.id }
        var likes: List<Like> = listOf()

        lifecycleScope.launch {
            likes = likeRepository.getDishLikes(dish.id)
        }.invokeOnCompletion {
            if (_binding == null)
                return@invokeOnCompletion

            callBack(List<Dish>(1) { dish }, List<Int>(1)  { likes.count() })
        }
    }

    private fun loadPopularDish(callBack: (dishes: List<Dish>, likes: List<Int>) -> Unit) {
        var likes: List<Like> = listOf()
        var dishesAndLikesCount: HashMap<Int, Int> = hashMapOf()

        lifecycleScope.launch {
            likes = likeRepository.getAllLikes()
        }.invokeOnCompletion {
            if (_binding == null)
                return@invokeOnCompletion

            likes.forEach {
                if (dishesAndLikesCount.containsKey(it.dishId))
                    dishesAndLikesCount[it.dishId] = dishesAndLikesCount[it.dishId]!! + 1
                else
                    dishesAndLikesCount.put(it.dishId, 1)
            }

            val popularestDish = dishesAndLikesCount.maxBy { x -> x.value }
            var dish: Dish? = null
            lifecycleScope.launch {
                dish = dishRepository.getDish(popularestDish.key)
            }.invokeOnCompletion {
                if (_binding == null)
                    return@invokeOnCompletion

                if (dish != null)
                    callBack(List<Dish>(1) { dish!! }, List<Int>(1)  { popularestDish.value })
            }
        }
    }

    private fun loadCourses(){
        val courses = mutableListOf(
            Course("Вегетарианство", R.drawable.vegetables) {
                val intent = Intent(this.requireContext(), VegetablesActivity::class.java)

                startActivity(intent)

            },
            Course("Простые завтраки", R.drawable.breakfast) {
                val intent = Intent(this.requireContext(), BreakfastActivity::class.java)

                startActivity(intent)
            }
        )

        val adapter = CoursesAdapter(courses)

        binding.rvHomeCourses.adapter = adapter

        binding.tvHomeCoursesLoading.visibility = INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}