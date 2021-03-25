package com.yogeshpaliyal.keypass.ui.backup

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.BackupActivityBinding
import com.yogeshpaliyal.keypass.databinding.LayoutBackupKeypharseBinding
import com.yogeshpaliyal.keypass.db_helper.createBackup
import com.yogeshpaliyal.keypass.utils.*
import kotlinx.coroutines.launch
import java.net.URLDecoder

class BackupActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val starter = Intent(context, BackupActivity::class.java)
            context?.startActivity(starter)
        }
    }

    private lateinit var binding: BackupActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BackupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val CHOOSE_BACKUPS_LOCATION_REQUEST_CODE = 26212

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.backup_preferences, rootKey)
            updateItems()
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            when (preference?.key) {
                "start_backup" -> {
                    startBackup()
                }
                "create_backup" -> {
                    context?.let {
                        if (it.canUserAccessBackupDirectory()) {
                            val selectedDirectory = Uri.parse(getBackupDirectory())
                            backup(selectedDirectory)
                        }
                    }
                }
                "backup_folder" -> {
                    changeBackupFolder()
                }
                getString(R.string.settings_verify_key_phrase) -> {
                    verifyKeyPhrase()
                }
                "stop_backup" -> {
                    stopBackup()
                }
            }
            return super.onPreferenceTreeClick(preference)
        }

        private fun updateItems() {
            val isBackupEnabled = context?.canUserAccessBackupDirectory() ?: false

            findPreference<Preference>("start_backup")?.isVisible = isBackupEnabled.not()

            findPreference<Preference>("create_backup")?.isVisible = isBackupEnabled
            findPreference<Preference>("create_backup")?.summary = "Last backup : ${getBackupTime().formatCalendar("dd MMM yyyy hh:mm aa")}"
            findPreference<Preference>("backup_folder")?.isVisible = isBackupEnabled
            val directory = URLDecoder.decode(getBackupDirectory(),"utf-8").split("/")
            val folderName = directory.get(directory.lastIndex)
            findPreference<Preference>("backup_folder")?.summary = folderName
            findPreference<Preference>("settings_verify_key_phrase")?.isVisible = false
            findPreference<Preference>("stop_backup")?.isVisible = isBackupEnabled
        }

        private fun startBackup(){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

            intent.addFlags(
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            try {
                startActivityForResult(intent, CHOOSE_BACKUPS_LOCATION_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CHOOSE_BACKUPS_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK){
                val contentResolver = context?.contentResolver
                val selectedDirectory = data?.data
                if (contentResolver != null && selectedDirectory != null) {
                    contentResolver.takePersistableUriPermission(
                        selectedDirectory,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    setBackupDirectory(selectedDirectory.toString())
                    backup(selectedDirectory)
                }
            }
        }

        private fun backup(selectedDirectory: Uri){

            val keyPair = getOrCreateBackupKey()

            val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile(
                "*/*",
                "key_pass_backup_${System.currentTimeMillis()}.keypass"
            )

            lifecycleScope.launch {
                context?.contentResolver?.let {
                    AppDatabase.getInstance().createBackup(keyPair.second,
                        it,
                        tempFile?.uri
                    )
                    setBackupTime(System.currentTimeMillis())
                    if (keyPair.first) {
                        val binding = LayoutBackupKeypharseBinding.inflate(layoutInflater)
                        binding.txtCode.text = getOrCreateBackupKey().second
                        binding.txtCode.setOnClickListener {
                            val clipboard =
                                ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                )
                            val clip = ClipData.newPlainText("KeyPass", binding.txtCode.text)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
                            .setPositiveButton(
                                "Yes"
                            ) { dialog, which -> dialog?.dismiss()
                            }.show()
                    }else{
                        Toast.makeText(context, getString(R.string.backup_completed), Toast.LENGTH_SHORT).show()
                    }
                }

                updateItems()
            }
        }

        private fun changeBackupFolder(){
            startBackup()
        }

        private fun verifyKeyPhrase(){
            Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show()
        }

        private fun stopBackup(){
            clearBackupKey()
            setBackupDirectory("")
            setBackupTime(-1)
            updateItems()
        }

    }
}