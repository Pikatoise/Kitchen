package com.example.kitchen.activities

import android.os.Bundle
import android.view.View.INVISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityMainBinding
import com.example.kitchen.ui.home.HomeFragment
import com.example.kitchen.ui.profile.ProfileFragment
import com.example.kitchen.ui.search.SearchFragment
import com.google.android.material.navigation.NavigationView.VISIBLE

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.bgButtonHome.setOnClickListener {
            if (currentFragmentId == R.id.button_home)
                return@setOnClickListener

            currentFragmentId = R.id.button_home

            navigationChange(R.id.button_home)

            loadFragment(HomeFragment(), false)
        }

        binding.bgButtonSearch.setOnClickListener {
            if (currentFragmentId == R.id.button_search)
                return@setOnClickListener

            currentFragmentId = R.id.button_search

            navigationChange(R.id.button_search)

            loadFragment(SearchFragment(), false)
        }

        binding.bgButtonProfile.setOnClickListener {
            if (currentFragmentId == R.id.button_profile)
                return@setOnClickListener

            currentFragmentId = R.id.button_profile

            navigationChange(R.id.button_profile)

            loadFragment(ProfileFragment(), false)
        }

        currentFragmentId = R.id.button_home
        loadFragment(HomeFragment(), true)
    }

    private fun navigationChange(buttonId: Int){
        val mainColor = ContextCompat.getColorStateList(this, R.color.main)
        val whiteColor = ContextCompat.getColorStateList(this, R.color.white)

        when (buttonId){
            R.id.button_home -> {
                binding.tvButtonHome.visibility = VISIBLE
                binding.buttonHome.backgroundTintList = mainColor
                binding.ivButtonHome.imageTintList = whiteColor

                binding.tvButtonProfile.visibility = INVISIBLE
                binding.buttonProfile.backgroundTintList = null
                binding.ivButtonProfile.imageTintList = mainColor

                binding.tvButtonSearch.visibility = INVISIBLE
                binding.buttonSearch.backgroundTintList = null
                binding.ivButtonSearch.imageTintList = mainColor
            }

            R.id.button_profile -> {
                binding.tvButtonProfile.visibility = VISIBLE
                binding.buttonProfile.backgroundTintList = mainColor
                binding.ivButtonProfile.imageTintList = whiteColor

                binding.tvButtonHome.visibility = INVISIBLE
                binding.buttonHome.backgroundTintList = null
                binding.ivButtonHome.imageTintList = mainColor

                binding.tvButtonSearch.visibility = INVISIBLE
                binding.buttonSearch.backgroundTintList = null
                binding.ivButtonSearch.imageTintList = mainColor
            }

            R.id.button_search -> {
                binding.tvButtonSearch.visibility = VISIBLE
                binding.buttonSearch.backgroundTintList = mainColor
                binding.ivButtonSearch.imageTintList = whiteColor

                binding.tvButtonHome.visibility = INVISIBLE
                binding.buttonHome.backgroundTintList = null
                binding.ivButtonHome.imageTintList = mainColor

                binding.tvButtonProfile.visibility = INVISIBLE
                binding.buttonProfile.backgroundTintList = null
                binding.ivButtonProfile.imageTintList = mainColor
            }
        }
    }

    private fun loadFragment(fragment: Fragment, isAppInit: Boolean) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

        if (isAppInit)
            transaction.add(R.id.frameLayout, fragment)
        else
            transaction.replace(R.id.frameLayout, fragment)

        transaction.commit()
    }
}