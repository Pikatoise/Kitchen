package com.example.kitchen.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kitchen.activities.AuthActivity
import com.example.kitchen.databinding.FragmentProfileBinding
import com.example.kitchen.sqlite.PreferencesRepository

class ProfileFragment constructor(private val onLoaded: () -> Unit) : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.ivProfileExit.setOnClickListener {
            exitProfile()
        }

        onLoaded()

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