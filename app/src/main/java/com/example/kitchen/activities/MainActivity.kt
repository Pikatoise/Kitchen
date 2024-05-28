package com.example.kitchen.activities

import android.os.Bundle
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityMainBinding
import com.example.kitchen.ui.home.HomeFragment
import com.example.kitchen.ui.profile.ProfileFragment
import com.example.kitchen.ui.search.SearchFragment
import com.google.android.material.navigation.NavigationView.VISIBLE
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val onLoadedCallBack = {
            setButtonClickAccess(true)
        }

        binding.bgButtonHome.setOnClickListener {
            if (currentFragmentId == R.id.button_home)
                return@setOnClickListener

            currentFragmentId = R.id.button_home

            navigationChange(R.id.button_home)

            setButtonClickAccess(false)

            loadFragment(HomeFragment(onLoadedCallBack), false)
        }

        binding.bgButtonSearch.setOnClickListener {
            if (currentFragmentId == R.id.button_search)
                return@setOnClickListener

            currentFragmentId = R.id.button_search

            navigationChange(R.id.button_search)

            loadFragment(SearchFragment(onLoadedCallBack), false)
        }

        binding.bgButtonProfile.setOnClickListener {
            if (currentFragmentId == R.id.button_profile)
                return@setOnClickListener

            currentFragmentId = R.id.button_profile

            navigationChange(R.id.button_profile)

            loadFragment(ProfileFragment(onLoadedCallBack), false)
        }

        currentFragmentId = R.id.button_home
        loadFragment(HomeFragment(onLoadedCallBack), true)
    }

    private fun setButtonClickAccess(status: Boolean){
        if (status){
            binding.bgButtonSearch.isClickable = true
            binding.bgButtonHome.isClickable = true
            binding.bgButtonProfile.isClickable = true
        }
        else{
            binding.bgButtonSearch.isClickable = false
            binding.bgButtonHome.isClickable = false
            binding.bgButtonProfile.isClickable = false
        }
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