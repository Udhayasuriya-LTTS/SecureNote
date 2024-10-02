package com.example.securenotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.securenotes.security.BiometricPropmptManager.*
import com.example.securenotes.databinding.ActivityLoginBinding
import com.example.securenotes.security.BiometricPropmptManager


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val promptManager by lazy { BiometricPropmptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.authenticateBtn.setOnClickListener {
            promptManager.showBiometricPrompt(
                "Touch ID for Secure Note",
                "Authentication is required to continue",
                "Place your finger on the fingerprint scanner to login"
            )

            promptManager.biometricResultLiveData.observe(this) { result ->
                when (result) {
                    is BiometricResult.AuthenticationError -> {
                        result.error
                        Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                    }

                    BiometricResult.AuthenticationFailed -> {
                        "Authentication failed"
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show()
                    }

                    BiometricResult.AuthenticationNotSet -> {
                        "Authentication Not Set"
                        Toast.makeText(this, "Authentication Not set", Toast.LENGTH_LONG).show()
                    }

                    BiometricResult.AuthenticationSuccess -> {
                        val i = Intent(
                            this,
                            MainActivity::class.java
                        )
                        startActivity(i)
                    }

                    BiometricResult.FeatureUnavailable -> {
                        "Feature Unavailable"
                        Toast.makeText(this, "Feature Unavailable", Toast.LENGTH_LONG).show()
                    }

                    BiometricResult.HardwareUnavailable -> {
                        "Hardware Unavailable"
                        Toast.makeText(this, "Hardware Unavailable", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                        Toast.makeText(this, "else", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

