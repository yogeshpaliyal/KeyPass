package com.yogeshpaliyal.keypass.ui.settings

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.yogeshpaliyal.keypass.MyApplication
import com.yogeshpaliyal.keypass.importer.AccountsImporter

class OpenKeyPassBackup<T : AccountsImporter>(val importer: T?) : ActivityResultContracts.OpenDocument() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        super.createIntent(context, input)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = importer?.allowedMimeType() ?: "*/*"

        intent.addFlags(
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        (context.applicationContext as? MyApplication)?.knownActivityLaunchTriggered()

        return intent
    }
}
