package com.example.ones.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.ones.R
import com.example.ones.data.preferences.UserPreference
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ones.data.preferences.dataStore
import com.example.ones.data.remote.api.RetrofitInstance
import androidx.activity.OnBackPressedCallback

class ProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        val cardEditProfile = view.findViewById<CardView>(R.id.card_edit_profile)
        val cardNotification = view.findViewById<CardView>(R.id.card_notification)
        val cardLogout = view.findViewById<CardView>(R.id.card_logout)
        val cardDeleteAccount = view.findViewById<CardView>(R.id.card_delete_account)

        // Menangani event klik card profile
        cardEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Menangani event klik card notification
        cardNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        // Menangani event klik card delete account
        cardDeleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun ini? Proses ini tidak bisa dibatalkan.")
                .setPositiveButton("Hapus") { dialog, _ ->
                    dialog.dismiss()
                    deleteAccount()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Menangani event klik card logout
        cardLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                userPreference.logout()
                Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()
                if (findNavController().currentDestination?.id == R.id.navigation_profile) {
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
            }
        }

        // Menangani tombol back agar kembali ke SettingsFragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.navigation_settings, false)
            }
        })

        return view
    }

    private fun deleteAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            val token = "Bearer ${userPreference.getToken()}"
            val userId = userPreference.getUserId()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Gagal mendapatkan informasi akun. Coba lagi nanti.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val response = RetrofitInstance.savingsApiService.deleteUser(userId, token)

                if (response.isSuccessful) {
                    userPreference.logout()
                    Toast.makeText(requireContext(), "Akun berhasil dihapus.", Toast.LENGTH_SHORT).show()
                    if (findNavController().currentDestination?.id == R.id.navigation_profile) {
                        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProfileFragment", "Error deleting account: $errorBody")
                    Toast.makeText(requireContext(), "Gagal menghapus akun. Coba lagi nanti.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error: ${e.message}")
                Toast.makeText(requireContext(), "Terjadi kesalahan jaringan.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
