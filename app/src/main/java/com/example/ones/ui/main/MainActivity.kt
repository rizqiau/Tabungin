package com.example.ones.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ones.R
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.databinding.ActivityMainBinding
import com.example.ones.ui.auth.AuthActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(dataStore)

        lifecycleScope.launchWhenCreated {
            userPreference.getSession().collect { user ->
                if (!user.isLogin) {
                    // Jika belum login, arahkan ke AuthActivity
                    navigateToAuth()
                } else {
                    // Jika sudah login, tetap di MainActivity
                    initMainActivity()
                }
            }
        }
    }

    private fun initMainActivity() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set BottomAppBar sebagai ActionBar
        setSupportActionBar(binding.bottomAppbar)

        val bottomNavigationView =
            binding.bottomAppbar.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNavigationView
            )

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Menyembunyikan Bottom Navigation di halaman tertentu
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_transaction,
                R.id.navigation_settings,
                R.id.navigation_history -> {
                    binding.bottomAppbar.visibility = View.VISIBLE
                    binding.fabAdd.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomAppbar.visibility = View.GONE
                    binding.fabAdd.visibility = View.GONE
                }
            }
        }

        // Setup AppBar dengan NavController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_transaction,
                R.id.navigation_settings,
                R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        binding.fabAdd.setOnClickListener {
            navController.navigate(R.id.navigation_add_transaction)
        }
    }

    private fun navigateToAuth() {
        Intent(this, AuthActivity::class.java).also {
            startActivity(it)
            finish() // Hentikan MainActivity agar tidak kembali ke sini
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
