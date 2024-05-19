package com.example.kitchen.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentRegisterBinding
import com.example.kitchen.models.User
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.UserRepository
import com.example.kitchen.supabase.repositories.UserRepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private lateinit var userRepository: UserRepository
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userRepository = UserRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.ivRegExit.setOnClickListener {
            toLogin()
        }

        binding.buttonRegRegister.setOnClickListener {
            val typedLogin = binding.etRegLogin.text.toString()
            val typedPassword = binding.etRegPassword.text.toString()
            val typedPasswordConfirm = binding.etRegPasswordConfirm.text.toString()

            if (typedLogin.length < 4){
                Toast.makeText(activity,"Логин слишком короткий!", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (typedPassword.length < 6){
                Toast.makeText(activity,"Пароль слишком короткий!", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (!typedPassword.equals(typedPasswordConfirm)){
                Toast.makeText(activity,"Пароли не совпадают!", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            var user: User? = null
            lifecycleScope.launch {
                user = userRepository.getUserByLogin(typedLogin)
            }.invokeOnCompletion {
                if (user != null)
                    Toast.makeText(activity, "Логин занят!", Toast.LENGTH_SHORT).show()
                else {
                    lifecycleScope.launch {
                        val newUser = User(-1, typedLogin, typedPassword)
                        userRepository.createUser(newUser)
                    }.invokeOnCompletion {
                        lifecycleScope.launch {
                            user = null
                            user = userRepository.getUserByLogin(typedLogin)
                        }.invokeOnCompletion {
                            lifecycleScope.launch {
                                //createProfile(user!!.id)
                            }.invokeOnCompletion {
                                toLogin()
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun toLogin(){
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout_auth, LoginFragment())
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        requireFragmentManager().executePendingTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}