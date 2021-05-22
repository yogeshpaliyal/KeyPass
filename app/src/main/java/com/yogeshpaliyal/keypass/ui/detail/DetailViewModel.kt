package com.yogeshpaliyal.keypass.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 11:52
*/
@HiltViewModel
class DetailViewModel @Inject constructor(application: Application, val appDb: AppDatabase) : AndroidViewModel(application) {

    val accountModel by lazy { MutableLiveData<AccountModel>() }

    fun loadAccount(accountId: Long?) {

        viewModelScope.launch(Dispatchers.IO) {
            accountModel.postValue(
                appDb.getDao().getAccount(accountId) ?: AccountModel()
            )
        }

    }


}