package com.yogeshpaliyal.keypass.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
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

fun Context.canUserAccessBackupDirectory(sp: SharedPreferences): Boolean {
    val backupDirectoryUri = getUri(getBackupDirectory(sp)) ?: return false
    val backupDirectory = DocumentFile.fromTreeUri(this, backupDirectoryUri)
    return backupDirectory != null && backupDirectory.exists() && backupDirectory.canRead() && backupDirectory.canWrite()
}

private fun getUri(string: String?): Uri? {
    val uri = string
    return if (TextUtils.isEmpty(uri)) {
        null
    } else {
        Uri.parse(uri)
    }
}
