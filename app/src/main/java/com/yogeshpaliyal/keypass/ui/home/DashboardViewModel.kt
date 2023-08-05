package com.yogeshpaliyal.keypass.ui.home

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.dbhelper.restoreBackup
import com.yogeshpaliyal.common.dbhelper.saveToDb
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
class DashboardViewModel @Inject constructor(
    application: Application,
    val appDb: com.yogeshpaliyal.common.AppDatabase
) :
    AndroidViewModel(application) {

    private val appDao = appDb.getDao()

    val mediator = MediatorLiveData<List<AccountModel>>()

    fun queryUpdated(
        keyword: String?,
        tag: String?,
        sortField: String?,
        sortAscending: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sortAscending) {
                mediator.postValue(appDao.getAllAccountsAscending(keyword ?: "", tag, sortField))
            } else {
                mediator.postValue(appDao.getAllAccountsDescending(keyword ?: "", tag, sortField))
            }
        }
    }

    suspend fun restoreBackup(
        list: List<AccountModel>
    ) {
        return appDb.saveToDb(list)
    }
}
