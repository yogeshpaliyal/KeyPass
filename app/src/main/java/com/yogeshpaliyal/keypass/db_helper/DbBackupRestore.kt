package com.yogeshpaliyal.keypass.db_helper

import android.content.ContentResolver
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.room.withTransaction
import com.google.gson.Gson
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.data.BackupData
import com.yogeshpaliyal.keypass.utils.getOrCreateBackupKey
import com.yogeshpaliyal.keypass.utils.getRandomString
import com.yogeshpaliyal.keypass.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 20-02-2021 19:31
*/

suspend fun AppDatabase.createBackup(key: String,contentResolver : ContentResolver,fileUri: Uri?) = withContext(Dispatchers.IO){
 fileUri ?: return@withContext false
    val data = getDao().getAllAccounts().first()

    val json = Gson().toJson(BackupData(this@createBackup.openHelper.readableDatabase.version, data))
    val fileStream = contentResolver.openOutputStream(fileUri)
    EncryptionHelper.doCryptoEncrypt(key,json, fileStream)

    return@withContext true
}


suspend fun AppDatabase.restoreBackup(key: String,contentResolver : ContentResolver,fileUri: Uri?) = withContext(Dispatchers.IO){
    fileUri ?: return@withContext false

    val restoredFile =    try {
          EncryptionHelper.doCryptoDecrypt(key, contentResolver.openInputStream(fileUri))
    }catch (e:Exception){
        e.printStackTrace()
        return@withContext false
    }
    val data = Gson().fromJson(restoredFile, BackupData::class.java)
    if (data.version == 3){
        for (datum in data.data) {
            datum.uniqueId = getRandomString()
        }
    }
    data.data.forEach {
        it.id = null
    }
    withTransaction {
        getDao().insertOrUpdateAccount(data.data)
    }
    return@withContext true
}

