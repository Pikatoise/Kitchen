package com.example.kitchen.ui.profile

import android.R.attr.previewImage
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
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
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


class ProfileFragment constructor() : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dishRepository: DishRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId = 0

    private lateinit var newAvatar: Uri
    private lateinit var ivNewAvatar: ImageView
    private lateinit var mGetContent: ActivityResultLauncher<String>

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
        }

        binding.ivProfileExit.setOnClickListener {
            exitProfile()
        }

        binding.mcvProfileEdit.setOnClickListener {
            showEditDialog()
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
            if (_binding == null)
                return@invokeOnCompletion

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

            if (userDishes.isEmpty()){
                binding.tvProfileDishLoading.text = "Пусто"
                callback(userDishes,likesCounts.toList())
                return@invokeOnCompletion
            }

            for(i in 0 until userDishes.count()){
                lifecycleScope.launch {
                    likesCounts[i] = likeRepository.getDishLikes(userDishes[i].id).count()
                }.invokeOnCompletion {
                    if (_binding == null)
                        return@invokeOnCompletion

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

    private fun showEditDialog() {
        val dialog = Dialog(this.requireContext())
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_edit_profile)


        val ivExit = dialog.findViewById<ImageView>(R.id.iv_dialog_edit_profile_exit)
        val tvNickLetter = dialog.findViewById<TextView>(R.id.tv_dialog_edit_profile_nick_letter)
        val ivAvatar = dialog.findViewById<ImageView>(R.id.iv_dialog_edit_profile_avatar)
        val mcvLoadPhoto = dialog.findViewById<MaterialCardView>(R.id.mcv_dialog_profile_edit_load_photo)
        val etName = dialog.findViewById<EditText>(R.id.et_dialog_profile_edit_name)
        val etPassword = dialog.findViewById<EditText>(R.id.et_dialog_profile_edit_password)
        val etPasswordConfirm = dialog.findViewById<EditText>(R.id.et_dialog_profile_edit_password_confirm)
        val mcvSave = dialog.findViewById<MaterialCardView>(R.id.mcv_dialog_profile_edit_save)

        ivNewAvatar = ivAvatar

        mcvLoadPhoto.setOnClickListener {
            mGetContent.launch("image/*")
        }

        ivExit.setOnClickListener {
            dialog.dismiss()
        }

        var profile: Profile? = null

        lifecycleScope.launch {
            profile = profileRepository.getProfile(profileId)
        }.invokeOnCompletion {
            if (profile == null)
                dialog.dismiss()

            tvNickLetter.text = profile!!.name[0].toString()

            if (profile!!.avatar.isNotEmpty())
                DownloadImageTask(ivAvatar)
                    .execute(
                        "https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_user_avatars/${profile!!.avatar}"
                    )

            etName.setText(profile!!.name)
        }


        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGetContent =
            registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) {
                    result -> ivNewAvatar.setImageURI(result)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}