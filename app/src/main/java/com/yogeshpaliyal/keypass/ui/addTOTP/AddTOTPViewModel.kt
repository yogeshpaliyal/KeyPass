package com.yogeshpaliyal.keypass.ui.addTOTP

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.constants.AccountType
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTOTPViewModel @Inject constructor(private val appDatabase: AppDatabase): ViewModel() {


    private val _goBack = MutableLiveData<Event<Unit>>()
    val goBack : LiveData<Event<Unit>> = _goBack

    private val _error = MutableLiveData<Event<Int>>()
    val error : LiveData<Event<Int>> = _error

    val secretKey = MutableLiveData<String>("")

    val accountName = MutableLiveData<String>("")

    fun saveAccount(){
        viewModelScope.launch {
            val secretKey = secretKey.value
            val accountName = accountName.value

            if (secretKey.isNullOrEmpty()){
                _error.postValue(Event(R.string.alert_black_secret_key))
                return@launch
            }

            if (accountName.isNullOrEmpty()){
                _error.postValue(Event(R.string.alert_black_secret_key))
                return@launch
            }

            val accountModel = AccountModel(password = secretKey, title = accountName,type = AccountType.TOPT)
            appDatabase.getDao().insertOrUpdateAccount(accountModel)
            _goBack.postValue(Event(Unit))
        }
    }

    fun setSecretKey(secretKey: String){
        this.secretKey.value = secretKey
    }

}