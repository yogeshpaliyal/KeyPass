package com.yogeshpaliyal.keypass.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.db_helper.createBackup
import com.yogeshpaliyal.keypass.db_helper.restoreBackup
import com.yogeshpaliyal.keypass.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.keypass.utils.email
import com.yogeshpaliyal.keypass.utils.getBackupDirectory
import com.yogeshpaliyal.keypass.utils.setBackupDirectory
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

            "share" -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "KeyPass Password Manager\n Offline, Secure, Open Source https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, "Share KeyPass"))
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun selectBackupDirectory(){
        val selectedDirectory = Uri.parse(getBackupDirectory())

        context?.let {
            if(it.canUserAccessBackupDirectory()){
                backup(selectedDirectory)
            }else{
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


    private fun selectRestoreFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

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

                setBackupDirectory(selectedDirectory.toString())
                backup(selectedDirectory)
            }
        } else if (requestCode == CHOOSE_RESTORE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val contentResolver = context?.contentResolver
            val selectedFile = data?.data
            if (contentResolver != null && selectedFile != null) {


                //val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile("*/keypass","key_pass_backup_${System.currentTimeMillis()}.keypass")

                lifecycleScope.launch {
                    AppDatabase.getInstance().restoreBackup(contentResolver, selectedFile)
                    Toast.makeText(context, getString(R.string.backup_restored), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun backup(selectedDirectory: Uri){

        val tempFile = DocumentFile.fromTreeUri(requireContext(), selectedDirectory)?.createFile(
            "*/*",
            "key_pass_backup_${System.currentTimeMillis()}.keypass"
        )

        lifecycleScope.launch {
            context?.contentResolver?.let { AppDatabase.getInstance().createBackup(
                it,
                tempFile?.uri
            )
                Toast.makeText(context, getString(R.string.backup_completed), Toast.LENGTH_SHORT).show()

            }

        }
    }

}