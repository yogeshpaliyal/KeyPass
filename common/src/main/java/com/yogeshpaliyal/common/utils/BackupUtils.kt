package com.yogeshpaliyal.common.utils

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.db_helper.createBackup
import java.security.SecureRandom

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 20-02-2021 22:10
*/

fun getRandomString(sizeOfRandomString: Int): String {
    val ALLOWED_CHARACTERS =
        "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM!@#$%&*_+"
    val random = SecureRandom()
    val sb = StringBuilder(sizeOfRandomString)
    for (i in 0 until sizeOfRandomString) sb.append(
        ALLOWED_CHARACTERS[
            random.nextInt(
                ALLOWED_CHARACTERS.length
            )
        ]
    )
    return sb.toString()
}

suspend fun Context?.canUserAccessBackupDirectory(): Boolean {
    this ?: return false
    val backupDirectoryUri = getUri(getBackupDirectory()) ?: return false
    val backupDirectory = DocumentFile.fromTreeUri(this, backupDirectoryUri)
    return backupDirectory != null && backupDirectory.exists() && backupDirectory.canRead() && backupDirectory.canWrite()
}

/**
 * @return Pair (Boolean to check if backup is for first time, is backup is for first time show user alert to save encryption key)
 * Second Value contains the encryption key
 */
suspend fun Context?.backupAccounts(
    appDb: AppDatabase,
    selectedDirectory: Uri,
    customFileName: String? = null
): Pair<Boolean, String>? {

    this ?: return null

    val keyPair = getOrCreateBackupKey()

    val fileName = (customFileName ?: "key_pass_backup_${System.currentTimeMillis()}") + ".keypass"

    val directory = DocumentFile.fromTreeUri(this, selectedDirectory)
    var docFile = directory?.findFile(fileName)
    if (docFile == null)
        docFile = DocumentFile.fromTreeUri(this, selectedDirectory)?.createFile(
            "*/*",
            fileName
        )

    val response = appDb.createBackup(
        keyPair.second,
        contentResolver,
        docFile?.uri
    )
    setBackupTime(System.currentTimeMillis())

    return keyPair
}

private fun getUri(string: String?): Uri? {
    val uri = string
    return if (TextUtils.isEmpty(uri)) {
        null
    } else {
        Uri.parse(uri)
    }
}
