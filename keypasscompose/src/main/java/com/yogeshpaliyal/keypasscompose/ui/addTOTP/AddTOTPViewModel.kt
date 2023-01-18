package com.yogeshpaliyal.keypasscompose.ui.addTOTP

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.Event
import com.yogeshpaliyal.keypasscompose.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTOTPViewModel @Inject constructor(private val appDatabase: AppDatabase) :
    ViewModel() {

    private val _goBack = MutableLiveData<Event<Unit>>()
    val goBack: LiveData<Event<Unit>> = _goBack

    private val _error = MutableLiveData<Event<Int>>()
    val error: LiveData<Event<Int>> = _error

    val secretKey = MutableLiveData<String>("")

    val accountName = MutableLiveData<String>("")

    fun loadOldAccount(accountId: String?) {
        accountId ?: return

        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.getDao().getAccount(accountId)?.let { accountModel ->
                accountName.postValue(accountModel.title ?: "")
            }
        }
    }

    fun saveAccount(accountId: String?) {
        viewModelScope.launch {
            val secretKey = secretKey.value
            val accountName = accountName.value

            if (accountId == null) {
                if (secretKey.isNullOrEmpty()) {
                    _error.postValue(Event(R.string.alert_black_secret_key))
                    return@launch
                }
            }

            if (accountName.isNullOrEmpty()) {
                _error.postValue(Event(R.string.alert_black_account_name))
                return@launch
            }

            val accountModel = if (accountId == null) {
                AccountModel(password = secretKey, title = accountName, type = AccountType.TOTP)
            } else {
                appDatabase.getDao().getAccount(accountId)?.also {
                    it.title = accountName
                }
            }

            accountModel?.let { appDatabase.getDao().insertOrUpdateAccount(it) }
            _goBack.postValue(Event(Unit))
        }
    }

    fun setSecretKey(secretKey: String) {
        this.secretKey.value = secretKey
    }

    fun setAccountName(accountName: String) {
        this.accountName.value = accountName
    }

    fun deleteAccount(accountId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            appDatabase.getDao().deleteAccount(accountId)
            onDeleted()
        }
    }
}
