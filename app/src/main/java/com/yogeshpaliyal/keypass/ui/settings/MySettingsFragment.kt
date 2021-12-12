package com.yogeshpaliyal.keypass.ui.settings

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogeshpaliyal.common.db_helper.createBackup
import com.yogeshpaliyal.common.db_helper.restoreBackup
import com.yogeshpaliyal.common.utils.*
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.LayoutBackupKeypharseBinding
import com.yogeshpaliyal.keypass.databinding.LayoutRestoreKeypharseBinding
import com.yogeshpaliyal.keypass.ui.backup.BackupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MySettingsFragment : PreferenceFragmentCompat() {
    private val CHOOSE_BACKUPS_LOCATION_REQUEST_CODE = 26212
    private val CHOOSE_RESTORE_FILE_REQUEST_CODE = 26213

    @Inject
    lateinit var appDb: com.yogeshpaliyal.common.AppDatabase

    @Inject
    lateinit var sp: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "feedback" -> {
                context?.email(
                    getString(R.string.feedback_to_keypass),
                    "yogeshpaliyal.foss@gmail.com"
                )
                return true
            }

            "backup" -> {
                BackupActivity.start(context)
                return true
            }

            getString(R.string.settings_restore_backup) -> {
                selectRestoreFile()
                return true
            }

            "share" -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "KeyPass Password Manager\n Offline, Secure, Open Source https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_keypass)))
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun selectBackupDirectory() {
        val selectedDirectory = Uri.parse(getBackupDirectory(sp))

        context?.let {
            if (it.canUserAccessBackupDirectory(sp)) {
                backup(selectedDirectory)
            } else {

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
        }
    }

    private fun selectRestoreFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        intent.addFlags(
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        try {
            startActivityForResult(intent, CHOOSE_RESTORE_FILE_REQUEST_CODE)
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

                setBackupDirectory(sp, selectedDirectory.toString())
                backup(selectedDirectory)
            }
        } else if (requestCode == CHOOSE_RESTORE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val contentResolver = context?.contentResolver
            val selectedFile = data?.data
            if (contentResolver != null && selectedFile != null) {

                val binding = LayoutRestoreKeypharseBinding.inflate(layoutInflater)

                MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(
                        "Restore"
                    ) { dialog, which ->
                        lifecycleScope.launch {
                            val result = appDb.restoreBackup(
                                binding.etKeyPhrase.text.toString(),
                                contentResolver,
                                selectedFile
                            )
                            if (result) {
                                dialog?.dismiss()
                                Toast.makeText(
                                    context,
                                    getString(R.string.backup_restored),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    getString(R.string.invalid_keyphrase),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }.show()
            }
        }
    }

    fun backup(selectedDirectory: Uri) {

        val keyPair = getOrCreateBackupKey(sp)

        val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile(
            "*/*",
            "key_pass_backup_${System.currentTimeMillis()}.keypass"
        )

        lifecycleScope.launch {
            context?.contentResolver?.let {
                appDb.createBackup(
                    keyPair.second,
                    it,
                    tempFile?.uri
                )
                if (keyPair.first) {
                    val binding = LayoutBackupKeypharseBinding.inflate(layoutInflater)
                    binding.txtCode.text = getOrCreateBackupKey(sp).second
                    binding.txtCode.setOnClickListener {
                        val clipboard =
                            getSystemService(requireContext(), ClipboardManager::class.java)
                        val clip = ClipData.newPlainText(
                            getString(R.string.app_name),
                            binding.txtCode.text
                        )
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
                        .setPositiveButton(
                            "Yes"
                        ) { dialog, which ->
                            dialog?.dismiss()
                        }.show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.backup_completed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
