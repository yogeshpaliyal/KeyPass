package com.yogeshpaliyal.keypass.autofill

import android.service.autofill.AutofillService
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.getAutoFillService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.service.autofill.Dataset
import android.service.autofill.FillContext
import android.service.autofill.SaveInfo
import android.view.View
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import com.yogeshpaliyal.common.AppDatabase

class KeyPassAutofillService : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: android.os.CancellationSignal, callback: FillCallback) {
        // Handle autofill requests
        val context = applicationContext
        val autofillManager = context.getSystemService(AutofillManager::class.java)

        val fillContexts: List<FillContext> = request.fillContexts
        val latestFillContext: FillContext = fillContexts[fillContexts.size - 1]
        val structure = latestFillContext.structure

        val fillResponseBuilder = FillResponse.Builder()

        // Fetch accounts and create datasets
        fetchAccountsAndShowSuggestions { accounts ->
            accounts.forEach { account ->
                val datasetBuilder = Dataset.Builder()
                structure.autofillIds.forEach { autofillId ->
                    datasetBuilder.setValue(autofillId, AutofillValue.forText(account.username))
                }
                val remoteViews = RemoteViews(packageName, R.layout.autofill_service)
                remoteViews.setTextViewText(R.id.autofill_text, account.username)
                datasetBuilder.setPresentation(remoteViews)
                fillResponseBuilder.addDataset(datasetBuilder.build())
            }
            callback.onSuccess(fillResponseBuilder.build())
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // Handle save requests
        val context = applicationContext
        val autofillManager = context.getSystemService(AutofillManager::class.java)

        val fillContexts: List<FillContext> = request.fillContexts
        val latestFillContext: FillContext = fillContexts[fillContexts.size - 1]
        val structure = latestFillContext.structure

        val account = AccountModel()
        structure.autofillIds.forEach { autofillId ->
            val autofillValue = structure.getAutofillValue(autofillId)
            if (autofillValue != null && autofillValue.isText) {
                account.username = autofillValue.textValue.toString()
            }
        }

        // Save account to the database
        saveAccountToDatabase(account)

        callback.onSuccess()
    }

    override fun onConnected() {
        // Handle service connected
    }

    override fun onDisconnected() {
        // Handle service disconnected
    }

    private fun fetchAccountsAndShowSuggestions(callback: (List<AccountModel>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val accounts = fetchAccounts()
            callback(accounts)
        }
    }

    private fun fetchAccounts(): List<AccountModel> {
        // Fetch accounts from the database or any other source
        val db = AppDatabase.getInstance(applicationContext)
        return db.dbDao().getAllAccountsList()
    }

    private fun saveAccountToDatabase(account: AccountModel) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(applicationContext)
            db.dbDao().insertOrUpdateAccount(account)
        }
    }
}
