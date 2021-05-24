package com.yogeshpaliyal.keypass.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 23:02
*/
@HiltViewModel
class DashboardViewModel @Inject constructor(application: Application, val appDb: AppDatabase) :
    AndroidViewModel(application) {

    val keyword by lazy {
        MutableLiveData<String>("")
    }
    val tag by lazy {
        MutableLiveData<String>()
    }


    val mediator = MediatorLiveData<LiveData<List<AccountModel>>>()

    init {

        mediator.addSource(keyword) {
            mediator.postValue(appDb.getDao().getAllAccounts(keyword.value, tag.value))
        }
        mediator.addSource(tag) {
            mediator.postValue(appDb.getDao().getAllAccounts(keyword.value, tag.value))
        }
        mediator.postValue(appDb.getDao().getAllAccounts(keyword.value, tag.value))


    }
}