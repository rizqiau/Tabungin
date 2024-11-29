package com.example.ones.ui.main

import android.content.Intent
import android.os.Bundle
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

        val bottomNavigationView =
            binding.bottomAppbar.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNavigationView
            )

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        // Perbaiki: hanya gunakan NavController jika berada di dalam MainActivity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Menangani aksi back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentBackStackEntry?.destination?.id == R.id.navigation_home) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })

        // Setup AppBar dengan navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_transaction,
                R.id.navigation_settings,
                R.id.navigation_profile
            )
        )

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
    private fun logout() {
        lifecycleScope.launchWhenCreated {
            userPreference.getSession().collect { user ->
                if (!user.isLogin) {
                    // Jika pengguna belum login, arahkan ke AuthActivity
                    navigateToAuth()
                } else {
                    // Jika sudah login, lanjutkan ke MainActivity
                    initMainActivity()
                }
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
