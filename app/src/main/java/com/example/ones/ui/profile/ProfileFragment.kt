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
import com.example.ones.ui.auth.fragments.LoginFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        val cardEditProfile = view.findViewById<CardView>(R.id.card_edit_profile)
        val cardNotification = view.findViewById<CardView>(R.id.card_notification)
        val cardLogout = view.findViewById<CardView>(R.id.card_logout)

        // Navigasi ke EditProfileFragment saat Edit Profile diklik
        cardEditProfile.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, EditProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Menampilkan pesan notifikasi saat Notification diklik
        cardNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        // Logout logic saat tombol Logout diklik
        cardLogout.setOnClickListener {
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

        return view
    }
}
