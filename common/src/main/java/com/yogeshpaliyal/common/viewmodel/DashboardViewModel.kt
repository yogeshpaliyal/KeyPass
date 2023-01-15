package com.yogeshpaliyal.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.common.data.AccountModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 23:02
*/
@HiltViewModel
class DashboardViewModel @Inject constructor(application: Application, val appDb: com.yogeshpaliyal.common.AppDatabase) :
    AndroidViewModel(application) {

    val keyword by lazy {
        MutableLiveData<String>("")
    }
    val tag by lazy {
        MutableLiveData<String>()
    }

    private val appDao = appDb.getDao()

    val mediator = MediatorLiveData<LiveData<List<AccountModel>>>()

    init {
        mediator.addSource(keyword) {
            mediator.postValue(appDao.getAllAccounts(keyword.value, tag.value))
        }
        mediator.addSource(tag) {
            mediator.postValue(appDao.getAllAccounts(keyword.value, tag.value))
        }
        mediator.postValue(appDao.getAllAccounts(keyword.value, tag.value))

        reloadData()
    }

    private fun reloadData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                mediator.postValue(appDao.getAllAccounts(keyword.value, tag.value))
            }
        }
    }
}
