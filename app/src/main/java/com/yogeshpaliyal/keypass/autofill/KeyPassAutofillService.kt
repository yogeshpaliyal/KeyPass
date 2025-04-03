package com.yogeshpaliyal.keypass.autofill

import android.app.assist.AssistStructure
import android.os.Build
import android.service.autofill.AutofillService
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.getAutoFillService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.yogeshpaliyal.common.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
/**
 * KeyPassAutofillService is an Android Autofill Service that provides autofill functionality for
 * username and password fields in apps. It retrieves saved credentials from a local database and
 * fills them into the appropriate fields when requested.
 *
 * @author Yogesh Paliyal
 * @since 1.0
 */
@AndroidEntryPoint
class KeyPassAutofillService : AutofillService() {

    @Inject
    lateinit var database: AppDatabase

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: android.os.CancellationSignal,
        callback: FillCallback
    ) {
        // Handle autofill requests
        // Get the structure from the request
        CoroutineScope(Dispatchers.Default).launch {
            val context: List<FillContext> = request.fillContexts
            val structure: AssistStructure = context[context.size - 1].structure

            // Traverse the structure looking for nodes to fill out
            val parsedStructure: ParsedStructure = parseStructure(structure) ?: run {
                callback.onFailure("No username or password field found")
                return@launch
            }

            // Fetch user data that matches the fields
            val account = fetchAccountsAndShowSuggestions(parsedStructure.usernameId.text.toString())

            // Build the presentation of the datasets
            val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            usernamePresentation.setTextViewText(android.R.id.text1, "my_username")
            val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username")

            // Add a dataset to the response
            val fillResponse: FillResponse = FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        .setValue(
                            parsedStructure.usernameId.autofillId!!,
                            AutofillValue.forText(account?.username),
                            usernamePresentation
                        )
                        .setValue(
                            parsedStructure.passwordId.autofillId!!,
                            AutofillValue.forText(account?.password),
                            passwordPresentation
                        )
                        .build()
                )
                .build()

            // If there are no errors, call onSuccess() and pass the response
            callback.onSuccess(fillResponse)
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
//        structure.autofillIds.forEach { autofillId ->
//            val autofillValue = structure.getAutofillValue(autofillId)
//            if (autofillValue != null && autofillValue.isText) {
//                account.username = autofillValue.textValue.toString()
//            }
//        }

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

    private suspend fun fetchAccountsAndShowSuggestions(username: String): AccountModel? {
        return withContext(Dispatchers.IO) {
            val accounts = fetchAccounts(username)
            return@withContext accounts
        }
    }

    private val passwordInputTypes: List<Int> = listOf(
        InputType.TYPE_NUMBER_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    )

    private val emailInputTypes: List<Int> = listOf(
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
    )

    private fun parseStructure(assistStructure: AssistStructure): ParsedStructure? {
        val count = assistStructure.windowNodeCount
        var usernameId: AssistStructure.ViewNode? = null
        var passwordId: AssistStructure.ViewNode? = null
        for (i in 0 until count) {
            val node = assistStructure.getWindowNodeAt(i).rootViewNode
            // Find the password, email or username fields
            var isPassword = passwordInputTypes.contains(node.inputType)
            if (!isPassword) {
                isPassword = node.autofillHints?.contains(View.AUTOFILL_HINT_PASSWORD) ?: false
            }
            if (isPassword) {
                passwordId = node
            }

            var isEmail = emailInputTypes.contains(node.inputType)
            if (!isEmail) {
                isEmail = node.autofillHints?.contains(View.AUTOFILL_HINT_EMAIL_ADDRESS) ?: false
            }
            if (!isEmail) {
                isEmail = node.autofillHints?.contains(View.AUTOFILL_HINT_USERNAME) ?: false
            }
            if (isEmail) {
                usernameId = node
            }
        }
        return if(usernameId != null && passwordId != null) ParsedStructure(usernameId, passwordId ) else null
    }

    private suspend fun fetchAccounts(username: String): AccountModel? {
        // Fetch accounts from the database or any other source
        val db = database
        return db.getDao().getAccountDetail(username = username)
    }

    private fun saveAccountToDatabase(account: AccountModel) {
        CoroutineScope(Dispatchers.IO).launch {
//            val db = AppDatabase.getInstance(applicationContext)
//            db.dbDao().insertOrUpdateAccount(account)
        }
    }
}

data class ParsedStructure(var usernameId: AssistStructure.ViewNode, var passwordId: AssistStructure.ViewNode)

data class UserData(var username: String, var password: String)
