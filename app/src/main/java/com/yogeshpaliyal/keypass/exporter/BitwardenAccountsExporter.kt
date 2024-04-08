package com.yogeshpaliyal.keypass.exporter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class BitwardenAccountsExporter : AccountsExporter {
    override fun getExporterTitle(): Int {
        return R.string.bitwarden_export
    }

    override fun getExporterDesc(): Int {
        return R.string.bitwarden_export_desc
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun export(context: Context, listOfAccounts: List<AccountModel>) : File {
        val title = context.getString(getExporterTitle()).lowercase()
        val path = context.filesDir
        val letDirectory = File(path, "${title}_exports")
        letDirectory.mkdirs()

        val currentTime = LocalDateTime.now()
        val dateTime = currentTime.format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmSS"))

        val exportFile: File = File(letDirectory, "${title}_export_${dateTime}.json")

        val uuid:UUID = UUID.randomUUID()

        var folderData : HashMap<String, Any?> = HashMap()
        folderData.put("id", uuid.toString())
        folderData.put("name", "KeyPass")

        var itemsData : MutableList<HashMap<String, Any?>> = mutableListOf()
        listOfAccounts.forEach {
            var accountData : HashMap<String, Any?> = HashMap()
            var loginData : HashMap<String, Any?> = HashMap()
            var uriData : HashMap<String, Any?> = HashMap()
            accountData.put("folderId", uuid.toString())
            accountData.put("type", 1)
            accountData.put("organizationId", it.uniqueId)
            accountData.put("id", it.id)
            accountData.put("name", it.title)
            accountData.put("notes", it.notes)
            accountData.put("favourite", false)
            accountData.put("fields", intArrayOf())

            loginData.put("username", it.username)
            loginData.put("password", it.password)
            loginData.put("totp", it.secret)

            uriData.put("uri", it.site)
            uriData.put("match", null)

            loginData.put("uris", uriData)
            accountData.put("login", loginData)
            itemsData.add(accountData)
        }

        val exportData: HashMap<String, Any?> = HashMap()
        exportData.put("folders", listOf(folderData))
        exportData.put("items", itemsData)

        val jsonString: String = JSONObject(exportData).toString()
        exportFile.writeText(jsonString)

        return exportFile
    }
}