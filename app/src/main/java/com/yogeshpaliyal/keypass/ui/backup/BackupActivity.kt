package com.yogeshpaliyal.keypass.ui.backup

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogeshpaliyal.common.utils.BACKUP_KEY_LENGTH
import com.yogeshpaliyal.common.utils.backupAccounts
import com.yogeshpaliyal.common.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.common.utils.clearBackupKey
import com.yogeshpaliyal.common.utils.formatCalendar
import com.yogeshpaliyal.common.utils.getBackupDirectory
import com.yogeshpaliyal.common.utils.getBackupTime
import com.yogeshpaliyal.common.utils.getOrCreateBackupKey
import com.yogeshpaliyal.common.utils.isAutoBackupEnabled
import com.yogeshpaliyal.common.utils.isKeyPresent
import com.yogeshpaliyal.common.utils.overrideAutoBackup
import com.yogeshpaliyal.common.utils.saveKeyphrase
import com.yogeshpaliyal.common.utils.setAutoBackupEnabled
import com.yogeshpaliyal.common.utils.setBackupDirectory
import com.yogeshpaliyal.common.utils.setBackupTime
import com.yogeshpaliyal.common.utils.setOverrideAutoBackup
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.BackupActivityBinding
import com.yogeshpaliyal.keypass.databinding.LayoutBackupKeypharseBinding
import com.yogeshpaliyal.keypass.databinding.LayoutCustomKeypharseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import javax.inject.Inject

@AndroidEntryPoint
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

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var appDb: com.yogeshpaliyal.common.AppDatabase

        private val CHOOSE_BACKUPS_LOCATION_REQUEST_CODE = 26212

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.backup_preferences, rootKey)
            updateItems()
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                getString(R.string.settings_start_backup) -> {
                    startBackup()
                }
                getString(R.string.settings_create_backup) -> {
                    lifecycleScope.launch {
                        if (context.canUserAccessBackupDirectory()) {
                            val selectedDirectory = Uri.parse(context.getBackupDirectory())
                            passwordSelection(selectedDirectory)
                        }
                    }
                }
                getString(R.string.settings_backup_folder) -> {
                    changeBackupFolder()
                }
                getString(R.string.settings_verify_key_phrase) -> {
                    verifyKeyPhrase()
                }
                getString(R.string.settings_stop_backup) -> {
                    lifecycleScope.launch {
                        stopBackup()
                    }
                }
                getString(R.string.settings_auto_backup) -> {
                    lifecycleScope.launch {
                        context.setAutoBackupEnabled(context.isAutoBackupEnabled().not())
                        updateItems()
                    }
                }
                getString(R.string.settings_override_auto_backup) -> {
                    lifecycleScope.launch {
                        context.setOverrideAutoBackup(context.overrideAutoBackup().not())
                        updateItems()
                    }
                }
            }
            return super.onPreferenceTreeClick(preference)
        }

        private suspend fun passwordSelection(selectedDirectory: Uri) {

            val isKeyPresent = context?.isKeyPresent() ?: return
            if (isKeyPresent) {
                backup(selectedDirectory)
                return
            }

            val builder = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.alert)
                .setMessage(getString(R.string.custom_generated_keyphrase_info))
                .setPositiveButton(
                    getString(R.string.custom_keyphrase)
                ) { dialog, which ->
                    dialog?.dismiss()
                    setCustomKeyphrase(selectedDirectory)
                }
                .setNegativeButton(R.string.generate_keyphrase) { dialog, which ->
                    dialog?.dismiss()
                    backup(selectedDirectory)
                }
            builder.show()
        }

        private fun setCustomKeyphrase(selectedDirectory: Uri) {
            val binding = LayoutCustomKeypharseBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, which ->

                    dialog?.dismiss()
                }.create()
            dialog.setOnShowListener {
                val positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                positiveBtn.setOnClickListener {
                    val keyphrase = binding.etKeyPhrase.text.toString().trim()
                    if (keyphrase.isEmpty()) {
                        Toast.makeText(context, R.string.alert_blank_keyphrase, Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }

                    if (keyphrase.length != BACKUP_KEY_LENGTH) {
                        Toast.makeText(
                            context,
                            R.string.alert_invalid_keyphrase,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    lifecycleScope.launch {
                        context?.saveKeyphrase(keyphrase)
                        backup(selectedDirectory)
                    }
                    dialog.dismiss()
                }
            }
            dialog.show()
        }

        fun backup(selectedDirectory: Uri) {
            lifecycleScope.launch {
                context.backupAccounts(appDb, selectedDirectory)?.let { keyPair ->
                    if (keyPair.first) {
                        val binding = LayoutBackupKeypharseBinding.inflate(layoutInflater)
                        binding.txtCode.text = context?.getOrCreateBackupKey()?.second ?: ""
                        binding.txtCode.setOnClickListener {
                            val clipboard =
                                ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                )
                            val clip = ClipData.newPlainText("KeyPass", binding.txtCode.text)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                getString(R.string.copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
                            .setPositiveButton(
                                getString(R.string.yes)
                            ) { dialog, which ->
                                updateItems()
                                dialog?.dismiss()
                            }.show()
                    } else {
                        updateItems()
                        Toast.makeText(
                            context,
                            getString(R.string.backup_completed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        private fun updateItems() {
            lifecycleScope.launch(Dispatchers.IO) {
                val isBackupEnabled =
                    context.canUserAccessBackupDirectory() && (context?.isKeyPresent() ?: false)

                val isAutoBackupEnabled = context.isAutoBackupEnabled()
                val overrideAutoBackup = context.overrideAutoBackup()

                val lastBackupTime = context.getBackupTime()
                val backupDirectory = context.getBackupDirectory()

                withContext(Dispatchers.Main) {

                    findPreference<Preference>(getString(R.string.settings_start_backup))?.isVisible =
                        isBackupEnabled.not()
                    findPreference<Preference>(getString(R.string.settings_stop_backup))?.isVisible =
                        isBackupEnabled

                    findPreference<Preference>(getString(R.string.settings_auto_backup))?.isVisible =
                        isBackupEnabled
                    findPreference<Preference>(getString(R.string.settings_auto_backup))?.summary =
                        if (isAutoBackupEnabled) getString(R.string.enabled) else getString(R.string.disabled)

                    findPreference<PreferenceCategory>(getString(R.string.settings_cat_auto_backup))?.isVisible =
                        isBackupEnabled && isAutoBackupEnabled

                    findPreference<Preference>(getString(R.string.settings_override_auto_backup))?.summary =
                        if (overrideAutoBackup) getString(R.string.enabled) else getString(R.string.disabled)

                    findPreference<Preference>(getString(R.string.settings_create_backup))?.isVisible =
                        isBackupEnabled
                    findPreference<Preference>(getString(R.string.settings_create_backup))?.summary =
                        getString(
                            R.string.last_backup_date,
                            lastBackupTime.formatCalendar("dd MMM yyyy hh:mm aa")
                        )
                    findPreference<Preference>(getString(R.string.settings_backup_folder))?.isVisible =
                        isBackupEnabled
                    val directory = URLDecoder.decode(backupDirectory, "utf-8").split("/")
                    val folderName = directory.get(directory.lastIndex)
                    findPreference<Preference>(getString(R.string.settings_backup_folder))?.summary =
                        folderName
                    findPreference<Preference>(getString(R.string.settings_verify_key_phrase))?.isVisible =
                        false
                    findPreference<Preference>(getString(R.string.settings_backup))?.isVisible =
                        isBackupEnabled
                }
            }
        }

        private fun startBackup() {
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
            if (requestCode == CHOOSE_BACKUPS_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val contentResolver = context?.contentResolver
                val selectedDirectory = data?.data
                if (contentResolver != null && selectedDirectory != null) {
                    contentResolver.takePersistableUriPermission(
                        selectedDirectory,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    lifecycleScope.launch {
                        context.setBackupDirectory(selectedDirectory.toString())
                        passwordSelection(selectedDirectory)
                    }
                }
            }
        }

        private fun changeBackupFolder() {
            startBackup()
        }

        private fun verifyKeyPhrase() {
            Toast.makeText(context, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }

        private suspend fun stopBackup() {
            context.clearBackupKey()
            context.setBackupDirectory("")
            context.setBackupTime(-1)
            context.setOverrideAutoBackup(false)
            context.setAutoBackupEnabled(false)
            updateItems()
        }
    }
}
