package com.yogeshpaliyal.keypass.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.db_helper.createBackup
import com.yogeshpaliyal.keypass.db_helper.restoreBackup
import com.yogeshpaliyal.keypass.utils.email
import kotlinx.coroutines.launch

class MySettingsFragment : PreferenceFragmentCompat() {
    private val CHOOSE_BACKUPS_LOCATION_REQUEST_CODE = 26212
    private val CHOOSE_RESTORE_FILE_REQUEST_CODE = 26213

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key){
            "feedback" -> {
                context?.email("Feedback to KeyPass", "techpaliyal@gmail.com")
                return true
            }

            "backup" -> {
                selectBackupDirectory()
                return true
            }

            "restore" -> {
                selectRestoreFile()
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun selectBackupDirectory(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        /*if (Build.VERSION.SDK_INT >= 26) {
            intent.putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                SignalStore.settings().getLatestSignalBackupDirectory()
            )
        }*/

        intent.addFlags(
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        try {
            startActivityForResult(intent, CHOOSE_BACKUPS_LOCATION_REQUEST_CODE)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun selectRestoreFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        /*if (Build.VERSION.SDK_INT >= 26) {
            intent.putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                SignalStore.settings().getLatestSignalBackupDirectory()
            )
        }*/

        intent.addFlags(
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        try {
            startActivityForResult(intent, CHOOSE_RESTORE_FILE_REQUEST_CODE)
        }catch (e: Exception){
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

                val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile("*/*","key_pass_backup_${System.currentTimeMillis()}.keypass")

                lifecycleScope.launch {
                    AppDatabase.getInstance().createBackup(contentResolver, tempFile?.uri)
                    Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CHOOSE_RESTORE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val contentResolver = context?.contentResolver
            val selectedFile = data?.data
            if (contentResolver != null && selectedFile != null) {


                //val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile("*/keypass","key_pass_backup_${System.currentTimeMillis()}.keypass")

                lifecycleScope.launch {
                    AppDatabase.getInstance().restoreBackup(contentResolver, selectedFile)
                    Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}