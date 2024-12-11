package com.example.ones.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ones.R
import com.example.ones.databinding.FragmentSignupBinding
import com.example.ones.ui.auth.AuthActivity
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.auth.AuthViewModel
import com.example.ones.data.model.Result

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        ).get(AuthViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupListeners()
        observeViewModel()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun setupListeners() {
        binding.createAccountButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.length < 3) {
                Toast.makeText(requireContext(), "Username must be at least 3 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(email)) {
                Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidPassword(password)) {
                Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.register(username, email, password)
        }
    }

    private fun observeViewModel() {
        authViewModel.signupResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.createAccountButton.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.createAccountButton.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.account_created_successfully), Toast.LENGTH_SHORT).show()

                    // Reset fields
                    binding.usernameEditText.text?.clear()
                    binding.emailEditText.text?.clear()
                    binding.passwordEditText.text?.clear()

                    (activity as AuthActivity).navigateToLogin()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.createAccountButton.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
