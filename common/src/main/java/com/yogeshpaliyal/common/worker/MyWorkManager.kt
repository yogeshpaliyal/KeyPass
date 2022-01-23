package com.yogeshpaliyal.common.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yogeshpaliyal.common.utils.isAutoBackupEnabled

suspend fun Context?.executeAutoBackup() {
    this ?: return

    if (this.isAutoBackupEnabled()) {
        val work = OneTimeWorkRequestBuilder<AutoBackupWorker>().build()

        WorkManager.getInstance(this.applicationContext)
            .enqueueUniqueWork("AutoBackupWorker", ExistingWorkPolicy.KEEP, work)
    }
}


fun Context?.migrateToDatastore(){
    this ?: return
    val work = OneTimeWorkRequestBuilder<SharedPrefMigrationWorker>().build()

    WorkManager.getInstance(this.applicationContext)
        .enqueueUniqueWork("SharedPrefMigrationWorker", ExistingWorkPolicy.KEEP, work)
}
