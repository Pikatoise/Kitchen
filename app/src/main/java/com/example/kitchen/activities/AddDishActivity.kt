package com.example.kitchen.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityAddDishBinding

class AddDishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddDishBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}