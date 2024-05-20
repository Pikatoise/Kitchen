package com.example.kitchen.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.MainActivity
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentLoginBinding
import com.example.kitchen.models.Profile
import com.example.kitchen.models.User
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.interfaces.UserRepository
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl
import com.example.kitchen.supabase.repositories.UserRepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private lateinit var userRepository: UserRepository
    private lateinit var profileRepository: ProfileRepository
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userRepository = UserRepositoryImpl(SupabaseModule.provideSupabaseDatabase())
        profileRepository = ProfileRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonLogAuth.setOnClickListener {
            var user: User? = null
            val typedLogin = binding.etLogLogin.text.toString()
            val typedPassword = binding.etLogPassword.text.toString()

            if (typedLogin.length == 0){
                Toast.makeText(activity,"Введите логин!", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (typedPassword.length == 0){
                Toast.makeText(activity,"Введите пароль!", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            lifecycleScope.launch {
                user = userRepository.getUserByLogin(typedLogin)
            }.invokeOnCompletion {
                if (user == null)
                    Toast.makeText(activity, "Пользователь не найден!", Toast.LENGTH_SHORT).show()
                else{
                    var profile: Profile? = null

                    if (user!!.password.equals(typedPassword)){
                        lifecycleScope.launch {
                            profile = profileRepository.getUserProfile(user!!.id)
                        }.invokeOnCompletion {
                            if (profile == null){
                                Toast.makeText(activity,"Ошибка. Профиль не найден!",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                PreferencesRepository(this.requireContext()).updateProfileId(profile!!.id)

                                toMainActivity()
                            }
                        }
                    }
                    else
                        Toast.makeText(activity,"Неверные данные!",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonLogReg.setOnClickListener {
            toRegister()
        }

        return binding.root
    }

    private fun toRegister(){
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout_auth, RegisterFragment())
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        requireFragmentManager().executePendingTransactions()
    }

    private fun toMainActivity(){
        val i = Intent(this.activity, MainActivity::class.java)

        startActivity(i)

        this.activity?.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        this.activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}