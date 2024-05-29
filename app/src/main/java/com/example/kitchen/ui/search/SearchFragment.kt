package com.example.kitchen.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.activities.DishActivity
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.databinding.FragmentSearchBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.DishCategory
import com.example.kitchen.models.Like
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.CategoryRepository
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.repositories.CategoryRepositoryImpl
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import kotlinx.coroutines.launch
import java.util.Locale

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var dishRepository: DishRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var dishes: List<Dish>
    private lateinit var likes: List<Int>
    private lateinit var categories: List<DishCategory>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val provider = SupabaseModule.provideSupabaseDatabase()
        dishRepository = DishRepositoryImpl(provider)
        categoryRepository = CategoryRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)

        loadAllData{dishes, likes, categories ->
            this.dishes = dishes
            this.likes = likes
            this.categories = categories

            binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus){
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)

                    val inputText = binding.etSearch.text.toString()

                    if (!inputText.isNullOrEmpty()){
                        requireActivity().window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )

                        loadSearchedDishes(inputText)

                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }

        return binding.root
    }

    private fun loadAllData(callback: (dishes: List<Dish>, likes: List<Int>, categories: List<DishCategory>) -> Unit){
        var allDishes: List<Dish> = listOf()
        var categories: List<DishCategory> = listOf()
        var likesCounts: IntArray

        lifecycleScope.launch {
            allDishes = dishRepository.getAllDishes()
            categories = categoryRepository.getAllCategories()
        }.invokeOnCompletion {
            if (_binding == null)
                return@invokeOnCompletion

            likesCounts = IntArray(allDishes.count())

            for(i in 0..allDishes.count() - 1){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(allDishes[i].id).count()
                }.invokeOnCompletion {
                    if (_binding == null)
                        return@invokeOnCompletion

                    if (i == allDishes.count() - 1)
                        callback(allDishes, likesCounts.toList(), categories)
                }
            }
        }
    }

    private fun loadSearchedDishes(input: String){
        val selectedDishes: MutableList<Dish> = mutableListOf()
        val selectedDishesLikeCounts: MutableList<Int> = mutableListOf()

        val inputFormatted = input.lowercase()

        for (i in 0..dishes.count() - 1){
            val dishNameFormatted = dishes[i].name.lowercase()

            if (dishNameFormatted.contains(inputFormatted)){
                selectedDishes.add(dishes[i])
                selectedDishesLikeCounts.add(likes[i])
            }
        }

        for (i in 0..categories.count() - 1){
            val categoryNameFormatted = categories[i].name.lowercase()

            if (categoryNameFormatted.contains(inputFormatted)){
                for (j in 0..dishes.count() - 1){
                    if (dishes[j].categoryId == categories[i].id && selectedDishes.all { x -> x.id != dishes[j].id }){
                        selectedDishes.add(dishes[j])
                        selectedDishesLikeCounts.add(likes[j])
                    }
                }
            }
        }

        if (!selectedDishes.isEmpty()){
            binding.tvSearchLoading.text = ""

            binding.rvSearch.adapter = DishesAdapter(selectedDishes, selectedDishesLikeCounts) {
                val intent = Intent(this.requireContext(), DishActivity::class.java)

                intent.putExtra("dishId", it)

                startActivity(intent)
            }
        }
        else{
            binding.rvSearch.adapter = null

            binding.tvSearchLoading.text = "Пусто"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}