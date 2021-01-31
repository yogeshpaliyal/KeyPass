package com.yogeshpaliyal.keypass.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.universal_adapter.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 23:02
*/
class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val dao by lazy {
        AppDatabase.getInstance().getDao()
    }


    suspend fun loadData(tag : String?)=  if (tag.isNullOrBlank())dao.getAllAccounts() else dao.getAllAccounts(tag)

}