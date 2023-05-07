package com.yogeshpaliyal.common.dbhelper

import android.content.ContentResolver
import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.Gson
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.DB_VERSION_3
import com.yogeshpaliyal.common.DB_VERSION_5
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.BackupData
import com.yogeshpaliyal.common.utils.getRandomString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 20-02-2021 19:31
*/

suspend fun AppDatabase.createBackup(
    key: String,
    contentResolver: ContentResolver,
    fileUri: Uri?
) =
    withContext(Dispatchers.IO) {
        fileUri ?: return@withContext false
        val data = getDao().getAllAccountsList()

        val json =
            Gson().toJson(BackupData(this@createBackup.openHelper.readableDatabase.version, data))
        val fileStream = contentResolver.openOutputStream(fileUri)
        EncryptionHelper.doCryptoEncrypt(key, json, fileStream)

        return@withContext true
    }

suspend fun AppDatabase.restoreBackup(
    key: String,
    contentResolver: ContentResolver,
    fileUri: Uri?
) = withContext(Dispatchers.IO) {
    fileUri ?: return@withContext false

    val restoredFile = try {
        EncryptionHelper.doCryptoDecrypt(key, contentResolver.openInputStream(fileUri))
    } catch (e: CryptoException) {
        e.printStackTrace()
        return@withContext false
    }

    return@withContext Gson().fromJson(restoredFile, BackupData::class.java)?.let { data ->
        if (data.version == DB_VERSION_3) {
            for (datum in data.data) {
                getRandomString().also { datum.uniqueId = it }
            }
        }
        if (data.version < DB_VERSION_5) {
            for (datum in data.data) {
                datum.type = AccountType.DEFAULT
            }
        }
        data.data.forEach {
            it.id = null
        }
        withTransaction {
            getDao().insertOrUpdateAccount(data.data)
        }
        true
    } ?: false
}
