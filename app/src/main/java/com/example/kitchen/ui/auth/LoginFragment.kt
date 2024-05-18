package com.example.kitchen.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.kitchen.AuthActivity
import com.example.kitchen.MainActivity
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.databinding.FragmentLoginBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

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
            toMainActivity()
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