package com.example.kitchen.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityDishBinding
import com.example.kitchen.models.Dish
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl
import kotlinx.coroutines.launch

class DishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishBinding
    private lateinit var dishRepository: DishRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId: Int = -1
    private var dishId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDishBinding.inflate(layoutInflater)

        dishRepository = DishRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        profileRepository = ProfileRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        preferencesRepository = PreferencesRepository(this)

        profileId = preferencesRepository.getProfileId()
        if (profileId <= 0)
            finish()

        dishId = intent.extras!!.getInt("dishId")
        if (dishId <= 0)
            finish()

        fillDishData()

        setContentView(binding.root)
    }

    private fun fillDishData(){
        var dish: Dish? = null
        lifecycleScope.launch {
            dish = dishRepository.getDish(dishId)
        }.invokeOnCompletion {
            if (dish == null)
                finish()

            DownloadImageTask(binding.ivDishDetailedImage)
                .execute(
                    "https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_dish_image/${dish!!.image}"
                )
        }
    }
}