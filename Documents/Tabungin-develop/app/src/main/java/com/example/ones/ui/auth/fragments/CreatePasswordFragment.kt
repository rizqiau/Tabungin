package com.example.ones.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ones.databinding.FragmentCreatePasswordBinding

class CreatePasswordFragment : Fragment() {

    private var _binding: FragmentCreatePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // Tombol kembali
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Tombol reset password
        binding.resetButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.passwordEditText2.text.toString()

            if (validatePasswords(password, confirmPassword)) {
                // Tambahkan logika untuk reset password di sini
                Toast.makeText(requireContext(), "Password successfully reset", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validatePasswords(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
