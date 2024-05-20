package com.example.kitchen.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kitchen.AuthActivity
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.databinding.FragmentProfileBinding
import com.example.kitchen.sqlite.PreferencesRepository

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.textProfile.text = "This is Profile Page!!!"

        binding.buttonExit.setOnClickListener {
            exitProfile()
        }

        return binding.root
    }

    private fun exitProfile(){
        PreferencesRepository(this.requireContext()).updateProfileId(-1)

        val i = Intent(this.requireContext(), AuthActivity::class.java)

        this.requireActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        this.requireActivity().finish()

        startActivity(i)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}