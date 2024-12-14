package com.example.ones.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.ones.R
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.databinding.FragmentProfileBinding
import com.example.ones.databinding.FragmentTransactionBinding
import com.example.ones.ui.auth.fragments.LoginFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        binding.editProfile.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, EditProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}
