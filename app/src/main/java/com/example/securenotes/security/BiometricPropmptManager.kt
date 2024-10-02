package com.example.securenotes.security

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class BiometricPropmptManager(private val activity: AppCompatActivity) {
    private val biometricResult = MutableLiveData<BiometricResult>()
     val biometricResultLiveData: LiveData<BiometricResult> = biometricResult


    fun showBiometricPrompt(title: String, subTitle:String,description: String,) {
        val manager = BiometricManager.from(activity)
        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)

            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
        if (Build.VERSION.SDK_INT < 30) {
            promptInfo.setNegativeButtonText("Cancel")
        }

        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                biometricResult.value = BiometricResult.HardwareUnavailable

            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                biometricResult.value = BiometricResult.FeatureUnavailable

            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                biometricResult.value = BiometricResult.AuthenticationNotSet

            }

            else -> {
                Unit
            }
        }
        val prompt = BiometricPrompt(activity, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                biometricResult.value = BiometricResult.AuthenticationError(errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                biometricResult.value = BiometricResult.AuthenticationSuccess
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                biometricResult.value = BiometricResult.AuthenticationFailed
            }
        })
        prompt.authenticate(promptInfo.build())

    }

    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}
