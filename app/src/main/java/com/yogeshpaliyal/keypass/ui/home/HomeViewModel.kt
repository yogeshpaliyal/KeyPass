package com.yogeshpaliyal.keypass.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.universal_adapter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 23:02
*/
@HiltViewModel
class HomeViewModel @Inject constructor(application: Application, val appDb : AppDatabase) : AndroidViewModel(application) {

    suspend fun loadData(tag : String?)=  if (tag.isNullOrBlank())appDb.getDao().getAllAccounts() else appDb.getDao().getAllAccounts(tag)

}