package com.yogeshpaliyal.common.dbhelper

import android.content.ContentResolver
import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.Gson
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.DB_VERSION_3
import com.yogeshpaliyal.common.DB_VERSION_5
import com.yogeshpaliyal.common.DB_VERSION_7
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
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

suspend fun AppDatabase.saveToDb(list: List<AccountModel>) = withContext(Dispatchers.IO) {
    withTransaction {
        getDao().insertOrUpdateAccount(list)
    }
}

suspend fun restoreBackup(
    key: String,
    contentResolver: ContentResolver,
    fileUri: Uri?
): List<AccountModel>? = withContext(Dispatchers.IO) {
    fileUri ?: return@withContext null

    val restoredFile = try {
        EncryptionHelper.doCryptoDecrypt(key, contentResolver.openInputStream(fileUri))
    } catch (e: CryptoException) {
        e.printStackTrace()
        return@withContext null
    }

    return@withContext Gson().fromJson(restoredFile, BackupData::class.java)?.let { data ->

        for (datum in data.data) {
            if (data.version >= DB_VERSION_3 && datum.uniqueId == null) {
                datum.uniqueId = getRandomString()
            }

            if (data.version < DB_VERSION_5) {
                datum.type = AccountType.DEFAULT
            }

            if (data.version < DB_VERSION_7) {
                if (datum.type == AccountType.TOTP) {
                    datum.type = AccountType.DEFAULT
                    datum.secret = datum.password
                    datum.password = null
                }
            }

            datum.id = null
        }

        data.data
    }
}
