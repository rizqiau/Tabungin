package com.example.ones

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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