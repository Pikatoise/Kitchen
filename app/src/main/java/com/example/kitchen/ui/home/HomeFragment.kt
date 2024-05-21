package com.example.kitchen.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.lists.LinePagerIndicatorDecoration
import com.example.kitchen.models.Dish
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileRepository: ProfileRepository
    private var profileId: Int = -1

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        profileRepository = ProfileRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        profileId = PreferencesRepository(this.requireContext()).getProfileId()
        binding.rvHomeRandomDishes.addItemDecoration(LinePagerIndicatorDecoration())
        //binding.textHome

        loadRandomDishes()

        return binding.root
    }

    private fun loadRandomDishes(){
        var listDishes: ArrayList<Dish> = arrayListOf()
        var listLikes: ArrayList<Int> = arrayListOf()

        listDishes.add(Dish(
            1,
            "Блины классические",
            "Очень сложный рецепт",
            "dish_1.jpg",
            30,
            2,
            1,
            7))
        listDishes.add(
            Dish(
                2,
                "Блины не классические",
                "Очень сложный рецепт",
                "dish_1.jpg",
                35,
                3,
                1,
                7))
        listDishes.add(
            Dish(
                3,
                "Блины ультра",
                "Очень сложный рецепт",
                "dish_1.jpg",
                60,
                10,
                1,
                7))

        listLikes.add(15)
        listLikes.add(3)
        listLikes.add(100)

        binding.rvHomeRandomDishes.adapter = DishesAdapter(listDishes, listLikes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}