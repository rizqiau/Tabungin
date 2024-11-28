package com.example.ones.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ones.R
import com.example.ones.ui.auth.fragments.LoginFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            // Load LoginFragment as the default fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, LoginFragment())
                .commit()
        }
    }
}
