package com.yogeshpaliyal.keypass.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.utils.email

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key){
            "feedback" -> {
                context?.email("Feedback to KeyPass", "techpaliyal@gmail.com")
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}