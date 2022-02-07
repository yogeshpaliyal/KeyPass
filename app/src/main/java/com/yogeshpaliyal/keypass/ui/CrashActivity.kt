package com.yogeshpaliyal.keypass.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.databinding.ActivityCrashBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder

@AndroidEntryPoint
class CrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashBinding

    companion object {
        private const val ARG_DATA = "arg_data"

        fun getIntent(context: Context, data: String?): Intent {
            return Intent(context, CrashActivity::class.java).also {
                it.putExtra(ARG_DATA, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtCrash.text = intent.extras?.getString(ARG_DATA)

        binding.btnSendFeedback.setOnClickListener {

            val deviceInfo = StringBuilder()
            try {
                deviceInfo.append("\n")
                deviceInfo.append("App Version: " + BuildConfig.VERSION_NAME)
                deviceInfo.append("\n")
                deviceInfo.append("Brand Name: " + Build.BRAND)
                deviceInfo.append("\n")
                deviceInfo.append("Manufacturer Name: " + Build.MANUFACTURER)
                deviceInfo.append("\n")
                deviceInfo.append("Device Name: " + Build.MODEL)
                deviceInfo.append("\n")
                deviceInfo.append("Device API Version: " + Build.VERSION.SDK_INT)
                deviceInfo.append("\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yogeshpaliyal.foss@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Crash Report in KeyPass")
            intent.putExtra(Intent.EXTRA_TEXT, binding.txtCrash.text.toString() + "$deviceInfo")

            startActivity(Intent.createChooser(intent, ""))
        }
    }
}
