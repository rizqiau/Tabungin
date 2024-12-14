package com.example.ones.ui.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.ones.R
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import kotlinx.coroutines.launch
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.*

class LanguageFragment : Fragment() {

    private lateinit var switchEnglish: Switch
    private lateinit var switchIndonesian: Switch
    private lateinit var userPreference: UserPreference // DataStore untuk menyimpan bahasa

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)

        // Inisialisasi userPreference dengan dataStore
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        // Inisialisasi view
        switchEnglish = view.findViewById(R.id.switch_english)
        switchIndonesian = view.findViewById(R.id.switch_indonesian)

        // Ambil bahasa saat ini dari UserPreference
        lifecycleScope.launch {
            val currentLanguage = userPreference.getLanguage().first()  // Ambil nilai pertama
            updateSwitches(currentLanguage.toString())
        }

        // Listener untuk Switch English
        switchEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchIndonesian.isChecked = false
                showConfirmationDialog("en") // Tampilkan dialog konfirmasi
            }
        }

        // Listener untuk Switch Indonesian
        switchIndonesian.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchEnglish.isChecked = false
                showConfirmationDialog("id") // Tampilkan dialog konfirmasi
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tambahkan OnBackPressedCallback di sini
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigasikan kembali ke halaman sebelumnya
                findNavController().navigateUp()
            }
        })
    }

    private fun showConfirmationDialog(languageCode: String) {
        val languageName = if (languageCode == "en") "English" else "Indonesian"
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Language Change")
            .setMessage("Are you sure you want to change the language to $languageName?")
            .setPositiveButton("Yes") { _, _ ->
                changeLanguage(languageCode)
            }
            .setNegativeButton("Cancel") { _, _ ->
                lifecycleScope.launch {
                    // Jika dibatalkan, kembalikan switch ke keadaan sebelumnya
                    val currentLanguage = userPreference.getLanguage().first()
                    updateSwitches(currentLanguage.toString())
                }
            }
            .show()
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        lifecycleScope.launch {
            userPreference.saveLanguage(languageCode) // Simpan bahasa yang dipilih
            updateSwitches(languageCode) // Perbarui tampilan Switch
        }

        val intent = activity?.intent
        activity?.overridePendingTransition(0, 0)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
        activity?.startActivity(intent)
    }

    private fun updateSwitches(languageCode: String) {
        Log.d("SwitchUpdate", "Current language: $languageCode")
        switchEnglish.isChecked = languageCode == "en"
        switchIndonesian.isChecked = languageCode == "id"
    }
}
