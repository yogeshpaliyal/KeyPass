package com.yogeshpaliyal.keypass.ui.detail

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.worker.executeAutoBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
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

    fun loadAccount(id: Long?, getAccount: (AccountModel) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccount(appDb.getDao().getAccount(id) ?: AccountModel())
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
            app.executeAutoBackup()
        }
    }
    fun generateQrCode(accountModel: AccountModel): Bitmap? {
        val accountJson = Gson().toJson(accountModel)
        println("JSON String:$accountJson")

        val hints = Hashtable<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(accountJson, BarcodeFormat.QR_CODE, 200, 200, hints)
            val barcodeEncoder = com.journeyapps.barcodescanner.BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}
