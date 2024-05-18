package com.example.kitchen.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.AuthActivity
import com.example.kitchen.MainActivity
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.databinding.FragmentLoginBinding
import com.example.kitchen.models.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonLogAuth.setOnClickListener {
            var users: List<User> = listOf()
            val typedLogin = binding.etLogLogin.text
            val typedPassword = binding.etLogPassword.text

            lifecycleScope.launch { users = supabase.from("Users").select{
                        filter {
                            eq("Login", typedLogin)
                        }
                    }.decodeList<User>()
            }.invokeOnCompletion {
                if (users.count() == 0){
                    Toast.makeText(activity,"Пользователь не найден!",Toast.LENGTH_SHORT).show()
                }
                else{
                    val user: User = users[0]

                    if (user.Password.equals(typedPassword))
                        toMainActivity()
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