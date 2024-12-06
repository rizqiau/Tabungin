package com.example.ones.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.ones.R
import androidx.navigation.fragment.findNavController
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.data.remote.api.RetrofitInstance
import com.example.ones.data.remote.request.UpdateUserRequest
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        // Inisialisasi view
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val currentPasswordEditText = view.findViewById<TextInputEditText>(R.id.currentPasswordEditText)
        val newPasswordEditText = view.findViewById<TextInputEditText>(R.id.newPasswordEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        // Ambil data dari preferences dan set ke form
        MainScope().launch {
            val session = userPreference.getSession().first()
            usernameEditText.setText(session.username)  // Menampilkan username dari session
            emailEditText.setText(session.email)        // Menampilkan email dari session
        }

        // Set listener untuk tombol Save
        saveButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString()
            val newEmail = emailEditText.text.toString()
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            if (newUsername.isBlank() || newEmail.isBlank() || currentPassword.isBlank() || newPassword.isBlank()) {
                Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(newUsername, newEmail, currentPassword, newPassword)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.navigation_profile, false)
            }
        })
    }

    private fun updateProfile(newUsername: String, newEmail: String, currentPassword: String, newPassword: String) {
        MainScope().launch {
            val token = "Bearer ${userPreference.getToken()}"
            val userId = userPreference.getUserId()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val updateRequest = UpdateUserRequest(
                username = newUsername,
                email = newEmail,
                password = currentPassword,
                newPassword = newPassword
            )

            try {
                val response = RetrofitInstance.savingsApiService.updateUser(userId, updateRequest)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Kembali ke halaman sebelumnya
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
