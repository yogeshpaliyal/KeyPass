package com.yogeshpaliyal.keypass.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yogeshpaliyal.common.data.UserSettings
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.databinding.ActivityCrashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
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

        binding.txtCrash.text = getCrashWithMetaData(intent.extras?.getString(ARG_DATA))

        binding.btnSendFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yogeshpaliyal.foss+keypass@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Crash Report in KeyPass")
            intent.putExtra(Intent.EXTRA_TEXT, binding.txtCrash.text.toString())

            startActivity(Intent.createChooser(intent, ""))
        }
    }

    private fun getCrashWithMetaData(crashData: String?): String {
        var userSettings: UserSettings? = null
        var currentAppVersion = "Not able to fetch"
        var lastAppVersion = "Not able to fetch"
        runBlocking {
            try {
                userSettings = getUserSettings()
                currentAppVersion = userSettings?.currentAppVersion.toString()
                lastAppVersion = userSettings?.lastAppVersion.toString()
            } catch (e: Exception) {
                currentAppVersion = e.message ?: "Not able to fetch"
                lastAppVersion = e.message ?: "Not able to fetch"
                e.printStackTrace()
            }
        }
        val installerPackageName = getInstallerPackageName(this, BuildConfig.APPLICATION_ID)
        val deviceInfo = StringBuilder()
        deviceInfo.append(crashData)
        try {
            deviceInfo.append("\n")
            deviceInfo.append("App Version from Build: " + BuildConfig.VERSION_NAME)
            deviceInfo.append("\n")
            deviceInfo.append("Current App Version: $currentAppVersion")
            deviceInfo.append("\n")
            deviceInfo.append("Previous App Version: $lastAppVersion")
            deviceInfo.append("\n")
            deviceInfo.append("Installed from: $installerPackageName")
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
        return deviceInfo.toString()
    }

    fun getInstallerPackageName(context: Context, packageName: String): String? {
        kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return context.packageManager.getInstallSourceInfo(packageName).installingPackageName
            }
            @Suppress("DEPRECATION")
            return context.packageManager.getInstallerPackageName(packageName)
        }
        return null
    }
}
