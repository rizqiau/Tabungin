package com.example.ones.ui.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
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


        cardEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile clicked!", Toast.LENGTH_SHORT).show()
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
