package com.example.kitchen.ui.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.FragmentHomeBinding
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.ProfileRepository
import com.example.kitchen.supabase.repositories.ProfileRepositoryImpl
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileRepository: ProfileRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        profileRepository = ProfileRepositoryImpl(SupabaseModule.provideSupabaseDatabase())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val profileId = PreferencesRepository(this.requireContext()).getProfileId()
        binding.textHome.text = "This is Home Page!!!\n Profile: ${profileId}"

        binding.buttonStart.setOnClickListener {
            var name = "UNDEFINED"

            val progressDialog = ProgressDialog.show(activity, "", "")
            progressDialog.show()
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            lifecycleScope.launch {
                val profile = profileRepository.getProfile(profileId)

                if (profile != null)
                    name = profile.name
            }.invokeOnCompletion {
                progressDialog.dismiss()
                
                Toast.makeText(this.requireContext(),name, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}