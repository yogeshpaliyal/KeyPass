package com.yogeshpaliyal.keypass.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.worker.executeAutoBackup
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
    val app: Application,
    val appDb: com.yogeshpaliyal.common.AppDatabase
) : AndroidViewModel(app) {

    private val _accountModel by lazy { MutableLiveData<AccountModel>() }
    val accountModel: LiveData<AccountModel> = _accountModel

    fun loadAccount(accountId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            _accountModel.postValue(
                appDb.getDao().getAccount(accountId) ?: AccountModel()
            )
        }
    }

    fun deleteAccount(onExecCompleted: () -> Unit) {
        viewModelScope.launch {
            accountModel.value?.let {
                withContext(Dispatchers.IO) {
                    appDb.getDao().deleteAccount(it)
                }
                autoBackup()
                onExecCompleted()
            }
        }
    }

    fun insertOrUpdate(onExecCompleted: () -> Unit) {
        viewModelScope.launch {
            accountModel.value?.let {
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
            app.executeAutoBackup()
        }
    }
}
