package com.yogeshpaliyal.keypass.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.AccountModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 11:52
*/
@HiltViewModel
class DetailViewModel @Inject constructor(
    app: Application,
    val appDb: com.yogeshpaliyal.common.AppDatabase
) : AndroidViewModel(app) {

    private val _accountModel by lazy { MutableLiveData<AccountModel>() }
    val accountModel: LiveData<AccountModel> = _accountModel

    fun loadAccount(accountId: Long?, getAccount: (AccountModel) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccount(appDb.getDao().getAccount(accountId) ?: AccountModel())
        }
    }

    fun deleteAccount(accountModel: AccountModel, onExecCompleted: () -> Unit) {
        viewModelScope.launch {
            accountModel.let {
                withContext(Dispatchers.IO) {
                    appDb.getDao().deleteAccount(it)
                }
                autoBackup()
                onExecCompleted()
            }
        }
    }

    fun insertOrUpdate(accountModel: AccountModel, onExecCompleted: () -> Unit) {
        viewModelScope.launch {
            accountModel.let {
                withContext(Dispatchers.IO) {
                    appDb.getDao().insertOrUpdateAccount(it)
                    autoBackup()
                }
            }
            onExecCompleted()
        }
    }

    private fun autoBackup() {
        viewModelScope.launch {
            // application.executeAutoBackup()
        }
    }
}
