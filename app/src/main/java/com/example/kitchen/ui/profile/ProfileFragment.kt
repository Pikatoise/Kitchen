package com.example.kitchen.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.activities.AuthActivity
import com.example.kitchen.activities.DishActivity
import com.example.kitchen.activities.UserFavoritesActivity
import com.example.kitchen.activities.UserLikesActivity
import com.example.kitchen.databinding.FragmentProfileBinding
import com.example.kitchen.lists.DishesAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.Profile
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.LikeRepository
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.LikeRepositoryImpl
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl
import kotlinx.coroutines.launch

class ProfileFragment constructor(private val onLoaded: () -> Unit) : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val provider = SupabaseModule.provideSupabaseDatabase()
        dishRepository = DishRepositoryImpl(provider)
        likeRepository = LikeRepositoryImpl(provider)
        profileRepository = ProfileRepositoryImpl(provider)

        preferencesRepository = PreferencesRepository(this.requireContext())

        profileId = preferencesRepository.getProfileId()
        if (profileId <= 0)
            return binding.root

        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        binding.mcvProfileFavorites.setOnClickListener {
            val intent = Intent(this.requireContext(), UserFavoritesActivity::class.java)

            startActivity(intent)
        }

        binding.mcvProfileLikes.setOnClickListener {
            val intent = Intent(this.requireContext(), UserLikesActivity::class.java)

            startActivity(intent)
        }

        loadUserDataAndDishes{dishes, likes ->
            if (dishes.isEmpty())
                binding.tvProfileDishLoading.text = "Пусто"
            else{
                binding.tvProfileDishLoading.text = ""

                binding.rvProfileMyDishes.adapter = DishesAdapter(dishes, likes) {
                    val intent = Intent(this.requireContext(), DishActivity::class.java)

                    intent.putExtra("dishId", it)

                    startActivity(intent)
                }
            }

            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            onLoaded()
        }

        binding.ivProfileExit.setOnClickListener {
            exitProfile()
        }

        return binding.root
    }

    private fun loadUserDataAndDishes(callback: (dishes: List<Dish>, likes: List<Int>) -> Unit){
        var userDishes: List<Dish> = arrayListOf()
        var likesCounts: IntArray
        var profile: Profile? = null

        lifecycleScope.launch {
            userDishes = dishRepository.getProfileDishes(profileId)
            profile = profileRepository.getProfile(profileId)
        }.invokeOnCompletion {
            if (profile != null)
                binding.tvProfileName.text = profile!!.name
            else
                binding.tvProfileName.text = "Undefinded"

            binding.tvProfileNickLetter.text = profile!!.name[0].toString()

            if (profile!!.avatar.isNotEmpty())
                DownloadImageTask(binding.ivProfileAvatar)
                    .execute(
                        "https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_user_avatars/${profile!!.avatar}"
                    )

            likesCounts = IntArray(userDishes.count())

            for(i in 0 until userDishes.count()){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(userDishes[i].id).count()
                }.invokeOnCompletion {
                    if (i == userDishes.count() - 1)
                        callback(userDishes, likesCounts.toList())
                }
            }
        }
    }

    private fun exitProfile(){
        PreferencesRepository(this.requireContext()).updateProfileId(-1)

        val i = Intent(this.requireContext(), AuthActivity::class.java)

        this.requireActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        this.requireActivity().finish()

        startActivity(i)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}