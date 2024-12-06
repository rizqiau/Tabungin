package com.example.ones.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ones.R
import com.example.ones.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // Login Button
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(username, password)
            }
        }

        // Forgot Password
        binding.forgotPasswordTextView.setOnClickListener {
            navigateToForgotPassword()
        }

        // Social Media Buttons
        binding.googleButton.setOnClickListener {
            Toast.makeText(requireContext(), "Google login not implemented", Toast.LENGTH_SHORT).show()
        }

        binding.appleButton.setOnClickListener {
            Toast.makeText(requireContext(), "Apple login not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin(username: String, password: String) {
        // Implement logic for login authentication
        Toast.makeText(requireContext(), "Logged in with $username", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToForgotPassword() {
        // Implement navigation to ForgotPasswordFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, ForgotPasswordFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
