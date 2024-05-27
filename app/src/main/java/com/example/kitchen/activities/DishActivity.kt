package com.example.kitchen.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityDishBinding
import com.example.kitchen.lists.IngredientsAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.DishCategory
import com.example.kitchen.models.Favorite
import com.example.kitchen.models.Ingredient
import com.example.kitchen.models.Like
import com.example.kitchen.models.Profile
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.CategoryRepository
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.FavoriteRepository
import com.example.kitchen.supabase.interfaces.IngredientRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.repositories.CategoryRepositoryImpl
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.FavoriteRepositoryImpl
import com.example.kitchen.supabase.repositories.IngredientRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl
import kotlinx.coroutines.launch


class DishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishBinding
    private lateinit var dishRepository: DishRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var categoryRepository: CategoryRepository
    private var profileId: Int = -1
    private var dishId: Int = -1
    private var isLike: Boolean = false
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDishBinding.inflate(layoutInflater)

        val provider = SupabaseModule.provideSupabaseDatabase()

        dishRepository = DishRepositoryImpl(provider)
        profileRepository = ProfileRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)
        favoriteRepository = FavoriteRepositoryImpl(provider)
        ingredientRepository = IngredientRepositoryImpl(provider)
        categoryRepository = CategoryRepositoryImpl(provider)
        preferencesRepository = PreferencesRepository(this)

        profileId = preferencesRepository.getProfileId()
        if (profileId <= 0)
            finish()

        dishId = intent.extras!!.getInt("dishId")
        if (dishId <= 0)
            finish()

        fillDishData()

        binding.ivDishDetailedExit.setOnClickListener {
            finish()
        }

        binding.clDishDetailedLikeButton.setOnClickListener {
            binding.clDishDetailedLikeButton.isClickable = false

            var likeCount = binding.tvDishDetailedLikeCount.text.toString().toInt()

            if (isLike){
                isLike = false

                likeCount -= 1

                lifecycleScope.launch {
                    likeRepository.removeDishLike(profileId, dishId)
                }.invokeOnCompletion {
                    binding.clDishDetailedLikeButton.isClickable = true
                }
            }
            else{
                isLike = true

                likeCount += 1

                lifecycleScope.launch {
                    likeRepository.setDishLike(profileId, dishId)
                }.invokeOnCompletion {
                    binding.clDishDetailedLikeButton.isClickable = true
                }
            }

            binding.tvDishDetailedLikeCount.text = likeCount.toString()

            updateLikeStatus()
        }

        binding.clDishDetailedFavoriteButton.setOnClickListener {
            binding.clDishDetailedFavoriteButton.isClickable = false

            if (isFavorite){
                isFavorite = false

                lifecycleScope.launch {
                    favoriteRepository.removeDishFavorite(profileId, dishId)
                }.invokeOnCompletion {
                    binding.clDishDetailedFavoriteButton.isClickable = true
                }
            }
            else{
                isFavorite = true

                lifecycleScope.launch {
                    favoriteRepository.setDishFavorite(profileId, dishId)
                }.invokeOnCompletion {
                    binding.clDishDetailedFavoriteButton.isClickable = true
                }
            }

            updateFavoriteStatus()
        }

        setContentView(binding.root)
    }

    fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter = listView.adapter ?: return

        val vg: ViewGroup = listView
        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, vg)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val par = listView.layoutParams
        par.height = totalHeight + (listView.dividerHeight * (adapter.count - 1))
        listView.layoutParams = par
        listView.requestLayout()
    }

    private fun fillIngredients(){
        var ingredients: List<Ingredient> = listOf()

        lifecycleScope.launch {
            ingredients = ingredientRepository.getDishIngredients(dishId)
        }.invokeOnCompletion {
            if (ingredients.isEmpty())
                return@invokeOnCompletion

            val adapter = IngredientsAdapter(this, ingredients)

            binding.lvDishDetailedIngredients.adapter = adapter

            justifyListViewHeightBasedOnChildren(binding.lvDishDetailedIngredients)
        }
    }

    private fun fillDishData(){
        var dish: Dish? = null

        lifecycleScope.launch {
            dish = dishRepository.getDish(dishId)
        }.invokeOnCompletion {
            if (dish == null)
                finish()

            binding.tvDishDetailedLetter.text = dish!!.name[0].toString()

            if (dish!!.image.isNotEmpty())
                DownloadImageTask(binding.ivDishDetailedImage)
                    .execute(
                        "https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_dish_image/${dish!!.image}"
                    )

            binding.tvDishDetailedTitleName.text = dish!!.name

            binding.tvDishDetailedTitleCookingTime.text = "${dish!!.cookingTime} мин."

            binding.tvDishDetailedRecipe.text = dish!!.recipe

            fillIngredients()

            var dishLikes: List<Like> = listOf()
            var dishCategory: DishCategory? = null
            var profile: Profile? = null
            var profileFavorites: List<Favorite> = listOf()
            lifecycleScope.launch {
                dishLikes = likeRepository.getDishLikes(dish!!.id)
                dishCategory = categoryRepository.getCategory(dish!!.categoryId)
                profile = profileRepository.getProfile(profileId)
                profileFavorites = favoriteRepository.getProfileFavorites(profileId)
            }.invokeOnCompletion {
                if (dishCategory != null)
                    binding.tvDishDetailedCategory.text = dishCategory!!.name
                else
                    binding.tvDishDetailedCategory.text = "Блюдо"

                binding.tvDishDetailedNickLetter.text = profile!!.name[0].toString()

                if (profile!!.avatar.isNotEmpty())
                    DownloadImageTask(binding.ivDishDetailedAvatar)
                        .execute(
                            "https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_user_avatars/${profile!!.avatar}"
                        )

                binding.tvDishDetailedNick.text = profile!!.name

                binding.tvDishDetailedLikeCount.text = dishLikes.count().toString()

                if (dishLikes.any { x -> x.profileId == profile!!.id })
                    isLike = true

                if (profileFavorites.any { x -> x.dishId == dish!!.id })
                    isFavorite = true

                updateLikeStatus()

                updateFavoriteStatus()
            }
        }
    }

    fun updateLikeStatus() {
        if (isLike)
            binding.ivDishDetailedLikeImage.setColorFilter(ContextCompat.getColor(this,R.color.likeActive))
        else
            binding.ivDishDetailedLikeImage.setColorFilter(ContextCompat.getColor(this,R.color.likeUnActive))
    }

    fun updateFavoriteStatus() {
        if (isFavorite)
            binding.ivDishDetailedFavoriteImage.setImageResource(R.drawable.ic_favorite_checked)
        else
            binding.ivDishDetailedFavoriteImage.setImageResource(R.drawable.ic_favorite)
    }
}