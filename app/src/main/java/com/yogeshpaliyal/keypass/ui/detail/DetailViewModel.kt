package com.yogeshpaliyal.keypass.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 11:52
*/
class DetailViewModel(application: Application) : AndroidViewModel(application) {

    val accountModel by lazy { MutableLiveData<AccountModel>() }

    fun loadAccount(accountId: Long?) {

        viewModelScope.launch(Dispatchers.IO) {
            accountModel.postValue(
                AppDatabase.getInstance().getDao().getAccount(accountId) ?: AccountModel()
            )
        }
    }
}
