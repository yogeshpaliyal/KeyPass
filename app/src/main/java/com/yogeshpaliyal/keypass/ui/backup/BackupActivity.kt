package com.yogeshpaliyal.keypass.ui.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.keypass.utils.getBackupDirectory

class BackupActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val starter = Intent(context, BackupActivity::class.java)
            context?.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.backup_preferences, rootKey)
            val selectedDirectory = Uri.parse(getBackupDirectory())

            if (context?.canUserAccessBackupDirectory() == true) {
                // already backup on
                findPreference<Preference>("create_backup")?.isVisible = true
                findPreference<Preference>("backup_folder")?.isVisible = true
                findPreference<Preference>("settings_verify_key_phrase")?.isVisible = true
                findPreference<Preference>("start_backup")?.isVisible = false
            } else {
                // backup is off
                findPreference<Preference>("create_backup")?.isVisible = false
                findPreference<Preference>("backup_folder")?.isVisible = false
                findPreference<Preference>("settings_verify_key_phrase")?.isVisible = false
                findPreference<Preference>("start_backup")?.isVisible = true
            }

        }
    }
}