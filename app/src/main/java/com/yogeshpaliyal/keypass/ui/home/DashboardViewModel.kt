package com.yogeshpaliyal.keypass.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.dbhelper.saveToDb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 23:02
*/
@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    val appDb: com.yogeshpaliyal.common.AppDatabase
) :
    AndroidViewModel(application) {

    private val appDao = appDb.getDao()

    val mediator = MediatorLiveData<List<AccountModel>>()

    fun queryUpdated(
        keyword: String?,
        tag: String?,
        sortField: String?,
        sortAscending: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sortAscending) {
                mediator.postValue(appDao.getAllAccountsAscending(keyword ?: "", tag, sortField))
            } else {
                mediator.postValue(appDao.getAllAccountsDescending(keyword ?: "", tag, sortField))
            }
        }
    }

    fun restoreBackup(
        list: List<AccountModel>
    ) {
        viewModelScope.launch {
            appDb.saveToDb(list)
        }
    }

    fun exportDataToCsv(
        context: android.content.Context,
        uri: android.net.Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                android.util.Log.d("CSVExport", "Starting CSV export using raw SQL cursor")

                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream == null) {
                    android.util.Log.e("CSVExport", "openOutputStream returned null for URI: $uri")
                    throw Exception("Cannot open output stream for URI: $uri")
                }

                // Clean up any empty garbage accounts from the database first
                try {
                    appDb.getDao().deleteEmptyAccounts()
                    android.util.Log.d("CSVExport", "Purged empty accounts from database successfully")
                } catch (e: Exception) {
                    android.util.Log.w("CSVExport", "Failed to purge empty accounts", e)
                }

                // Use Room DAO instead of raw SQL query
                val list = appDb.getDao().getAllAccountsList()
                android.util.Log.d("CSVExport", "DAO returned ${list.size} accounts")

                var rowCount = 0
                outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    // Write header
                    writer.write(csvLine("Title", "Username", "Password", "URL", "Notes", "Group", "TOTP"))
                    writer.newLine()

                    // Write each row from DAO list
                    for (account in list) {
                        val title = account.title ?: ""
                        val username = account.username ?: ""
                        val password = account.password ?: ""
                        val site = account.site ?: ""
                        val notes = account.notes ?: ""
                        val tags = account.tags ?: ""
                        val secret = account.secret ?: ""

                        // Skip if all fields are blank/empty (garbage rows)
                        if (title.isBlank() && username.isBlank() && password.isBlank() && site.isBlank() && notes.isBlank() && tags.isBlank() && secret.isBlank()) {
                            continue
                        }

                        if (rowCount == 0) {
                            android.util.Log.d("CSVExport", "First non-empty row values: id=${account.id}, title='$title', username='$username', site='$site'")
                        }

                        writer.write(csvLine(title, username, password, site, notes, tags, secret))
                        writer.newLine()
                        rowCount++
                    }
                    writer.flush()
                }

                android.util.Log.d("CSVExport", "Finished writing $rowCount rows")

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                android.util.Log.e("CSVExport", "Export failed", e)
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    private fun csvEscape(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }

    private fun csvLine(vararg fields: String): String {
        return fields.joinToString(",") { csvEscape(it) }
    }
}
