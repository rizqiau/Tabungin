package com.example.ones.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.ones.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val cardEditProfile = view.findViewById<CardView>(R.id.card_edit_profile)
        val cardNotification = view.findViewById<CardView>(R.id.card_notification)
        val cardLogout = view.findViewById<CardView>(R.id.card_logout)

        // Navigasi ke EditProfileFragment.kt saat Edit Profile diklik
        cardEditProfile.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()


            transaction.replace(R.id.nav_host_fragment_activity_main, EditProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cardNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        cardLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout clicked!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
