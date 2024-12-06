package com.example.ones.ui.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ones.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Profile CardView
        val cardProfile = view.findViewById<CardView>(R.id.card_profile)
        cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        // Tema CardView
        val cardTema = view.findViewById<CardView>(R.id.card_tema)
        cardTema.setOnClickListener {
            // Tambahkan navigasi ke fragment tema
        }

        // Language CardView
        val cardLanguage = view.findViewById<CardView>(R.id.card_language)
        cardLanguage.setOnClickListener {
            // Tambahkan navigasi ke fragment pengaturan bahasa
        }


    }
}

