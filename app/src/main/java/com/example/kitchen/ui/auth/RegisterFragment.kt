package com.example.kitchen.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentLoginBinding
import com.example.kitchen.databinding.FragmentRegisterBinding
import com.example.kitchen.dtos.ProfileDto
import com.example.kitchen.dtos.UserDto
import com.example.kitchen.models.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    val supabase = createSupabaseClient(
        supabaseUrl = "https://gkeqyqnfnwgcbpgbnxkq.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdrZXF5cW5mbndnY2JwZ2JueGtxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTU5NDI3NjgsImV4cCI6MjAzMTUxODc2OH0.wkGX4ZbmEYC2A5ThtPJxe_f0xXpK0uFQ-7lP8n6hdPE"
    ) {
        install(Postgrest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            var count: Long? = null
            lifecycleScope.launch {
                count = isUserExist(typedLogin)
            }.invokeOnCompletion {
                if (count != null)
                    Toast.makeText(activity, "Логин занят!", Toast.LENGTH_SHORT).show()
                else {
                    lifecycleScope.launch {
                        createUser(typedLogin,typedPassword)
                    }.invokeOnCompletion {
                        lifecycleScope.launch {
                            user = getUser(typedLogin)
                        }.invokeOnCompletion {
                            lifecycleScope.launch {
                                createProfile(user!!.id)
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

    suspend fun createProfile(userId: Int): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                val profile = ProfileDto("name_${userId}","",userId)

                supabase.from("Profiles").insert(profile)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    suspend fun createUser(login: String, password: String): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                val user = UserDto(login,password)

                supabase.from("Users").insert(user)

                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    suspend fun isUserExist(login: String): Long?{
        return withContext(Dispatchers.IO){
            supabase.from("Users").select{
                filter {
                    eq("Login", login)
                }
            }.countOrNull()
        }
    }

    suspend fun getUser(login: String): User{
        return withContext(Dispatchers.IO){
            supabase.from("Users").select{
                filter {
                    eq("Login", login)
                }
            }.decodeSingle()
        }
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