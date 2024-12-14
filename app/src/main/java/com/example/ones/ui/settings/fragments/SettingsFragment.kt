package com.example.ones.ui.settings.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ones.R
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.databinding.FragmentSettingsBinding
import com.example.ones.ui.auth.fragments.LoginFragment
import com.example.ones.viewmodel.settings.SettingsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cardLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_languageFragment)
        }

        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.switchDarkMode.isChecked = currentTheme == Configuration.UI_MODE_NIGHT_YES

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.btnLogout.setOnClickListener {
            MainScope().launch {
                // Menghapus sesi atau data login
                userPreference.logout()

                // Menampilkan pesan Toast
                Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()

                // Navigasi kembali ke LoginFragment
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment_activity_main, LoginFragment())
                transaction.addToBackStack(null)  // Menambahkan fragment ke back stack jika ingin kembali ke profil
                transaction.commit()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}