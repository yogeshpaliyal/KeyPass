package com.yogeshpaliyal.keypass.ui.addTOTP

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.getRandomString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTOTPViewModel @Inject constructor(private val appDatabase: AppDatabase) :
    ViewModel() {

    fun loadOldAccount(accountId: String?, loadAccount: (accountModel: AccountModel) -> Unit) {
        accountId ?: return

        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.getDao().getAccount(accountId)?.let { accountModel ->
                loadAccount(accountModel)
            }
        }
    }

    fun saveAccount(accountModel: AccountModel, onComplete: () -> Unit) {
        viewModelScope.launch {
            val accountModelDb = if (accountModel.uniqueId == null) {
                AccountModel(uniqueId = getRandomString(), password = accountModel.password, title = accountModel.title, type = AccountType.TOTP)
            } else {
                appDatabase.getDao().getAccount(accountModel.uniqueId)?.also {
                    it.title = accountModel.title
                    it.password = accountModel.password
                }
            }

            accountModelDb?.let { appDatabase.getDao().insertOrUpdateAccount(it) }
            onComplete()
        }
    }

    fun deleteAccount(accountId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            appDatabase.getDao().deleteAccount(accountId)
            onDeleted()
        }
    }
}
