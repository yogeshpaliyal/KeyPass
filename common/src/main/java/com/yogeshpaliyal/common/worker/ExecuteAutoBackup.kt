package com.yogeshpaliyal.common.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yogeshpaliyal.common.utils.getUserSettings

suspend fun Context?.executeAutoBackup() {
    val userSettings = this?.getUserSettings() ?: return


    if (userSettings.autoBackupEnable) {
        val work = OneTimeWorkRequestBuilder<AutoBackupWorker>().build()

        WorkManager.getInstance(this.applicationContext)
            .enqueueUniqueWork("AutoBackupWorker", ExistingWorkPolicy.KEEP, work)
    }
}
