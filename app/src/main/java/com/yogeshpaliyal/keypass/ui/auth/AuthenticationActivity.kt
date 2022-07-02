package com.yogeshpaliyal.keypass.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.ActivityAuthenticationBinding
import com.yogeshpaliyal.keypass.ui.nav.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

private const val AUTHENTICATION_RESULT = 707

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val biometricManager by lazy {
        BiometricManager.from(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                    // finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    onAuthenticated()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_name))
            .setSubtitle(getString(R.string.login_to_enter_keypass))
            .setAllowedAuthenticators(DEVICE_CREDENTIAL or BIOMETRIC_WEAK or BIOMETRIC_STRONG)
            .build()

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

        biometricPrompt.authenticate(promptInfo)

        binding.btnRetry.setOnClickListener {
            val allowedAuths = DEVICE_CREDENTIAL or BIOMETRIC_WEAK or BIOMETRIC_STRONG
            val canAuthentication =
                biometricManager.canAuthenticate(allowedAuths)
            when (canAuthentication) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.e(
                        "MY_APP_TAG",
                        "$canAuthentication Biometric features are currently unavailable."
                    )
                    // Prompts the user to create credentials that your app accepts.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        startActivityForResult(enrollIntent, AUTHENTICATION_RESULT)
                    } else {
                        Toast.makeText(
                            this,
                            "Please set password for your device first from phone settings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun onAuthenticated() {
        // binding.passCodeView.isVisible = false
        val dashboardIntent = Intent(this, DashboardActivity::class.java)
        startActivity(dashboardIntent)
        finish()
    }
}
