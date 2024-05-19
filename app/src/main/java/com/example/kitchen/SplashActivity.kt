package com.example.kitchen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kitchen.databinding.ActivitySplashBinding
import com.example.kitchen.supabase.SupabaseModule

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate().setDuration(1500).alpha(1f ).withEndAction {
            val i = Intent(this, AuthActivity::class.java)

            startActivity(i)

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out,)

            finish()
        }
    }
}