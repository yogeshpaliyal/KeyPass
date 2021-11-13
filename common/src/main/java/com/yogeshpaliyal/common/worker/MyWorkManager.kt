package com.yogeshpaliyal.common.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yogeshpaliyal.common.utils.isAutoBackupEnabled
import com.yogeshpaliyal.keypass.worker.AutoBackupWorker

fun Context?.executeAutoBackup(sp: SharedPreferences) {
    this ?: return

    if (sp.isAutoBackupEnabled()) {
        val work = OneTimeWorkRequestBuilder<AutoBackupWorker>().build()

        WorkManager.getInstance(this.applicationContext)
            .enqueueUniqueWork("AutoBackupWorker", ExistingWorkPolicy.KEEP, work)
    }
}
