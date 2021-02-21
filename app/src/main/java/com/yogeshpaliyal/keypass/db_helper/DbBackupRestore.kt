package com.yogeshpaliyal.keypass.db_helper

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.Gson
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.utils.getOrCreateBackupKey
import com.yogeshpaliyal.keypass.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 20-02-2021 19:31
*/

suspend fun AppDatabase.createBackup(contentResolver : ContentResolver,fileUri: Uri?) = withContext(Dispatchers.IO){
 fileUri ?: return@withContext false
    val data = getDao().getAllAccounts().first()
    val json = Gson().toJson(data)

    val fileStream = contentResolver.openOutputStream(fileUri)
    EncryptionHelper.doCryptoEncrypt(getOrCreateBackupKey(),json, fileStream)

    return@withContext true
}


suspend fun AppDatabase.restoreBackup(contentResolver : ContentResolver,fileUri: Uri?) = withContext(Dispatchers.IO){
    fileUri ?: return@withContext false

    EncryptionHelper.doCryptoDecrypt(getOrCreateBackupKey(), contentResolver.openInputStream(fileUri)).logD("DecryptedFile")

    return@withContext true
}