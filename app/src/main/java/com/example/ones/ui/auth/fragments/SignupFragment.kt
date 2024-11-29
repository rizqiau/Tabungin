package com.example.ones.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ones.R
import com.example.ones.data.model.Result
import com.example.ones.databinding.FragmentSignupBinding
import com.example.ones.ui.auth.AuthActivity
import com.example.ones.ui.main.MainActivity
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.auth.AuthViewModel

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        ).get(AuthViewModel::class.java)

        binding.createAccountButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Validasi input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Memanggil register
            authViewModel.register(username, email, password)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.signupResult.observe(viewLifecycleOwner, Observer { result ->
            Log.d("SignupFragment", "Received signup result: $result")
            if (result.isSuccess) {
                Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                (activity as AuthActivity).navigateToLogin()
            } else {
                Toast.makeText(requireContext(), "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
