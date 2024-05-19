package com.example.kitchen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.example.kitchen.databinding.ActivityAuthBinding
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.repositories.UserRepositoryImpl
import com.example.kitchen.ui.auth.LoginFragment
import com.example.kitchen.ui.auth.RegisterFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SupabaseModule.provideSupabaseDatabase().init()

        binding = ActivityAuthBinding.inflate(layoutInflater)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout_auth, LoginFragment())
        transaction.commit()
        supportFragmentManager.executePendingTransactions()

        setContentView(binding.root)
    }
}